import java.util.*;

/** DES - implementation of the DES block cipher in Java.
 *  <p> Illustrative code for the DES block cipher. Handles a single
 *  block encryption or decryption, with diagnostic logging of intermediate
 *  values if required.
 *  <p>
 *  DES is a block cipher with an 8 byte block size. The key length
 *  is 8 bytes, but only 56 bits are used as the parity bit in each
 *  byte is ignored.
 *  <p>
 *  This algorithm has been seriously analysed over the last 30 years,
 *  and no significant weaknesses have been reported. Its only known
 *  flaw is that the key length of 56 bits makes it relatively easy to
 *  brute-force it.
 *  <p>
 *  DES was written by IBM and first released in 1976. The algorithm is
 *  freely usable for both single and triple encryption.
 *  <p>
 *  <b>References:</b>
 *  <ol>
 *    <li> <a href="mailto:schneier@counterpane.com">Bruce Schneier</a>,
 *         "Chapter 12 Data Encryption Standard,"
 *         <cite>Applied Cryptography, 2nd edition</cite>,
 *         John Wiley &amp; Sons, 1996.
 *         <p>
 *    <li> NIST FIPS PUB 46-2 (supercedes FIPS PUB 46-1),
 *         "Data Encryption Standard",
 *         U.S. Department of Commerce, December 1993.<br>
 *         <a href="http://www.itl.nist.gov/div897/pubs/fip46-2.htm">
 *         http://www.itl.nist.gov/div897/pubs/fip46-2.htm</a>
 *  </ol>
 *  <p>
 *  Code is based on previous versions in C by Lawrie Brown, as included in:
 *  <cite>J Seberry, J Pieprzyk, "Cryptography", Prentice-Hall 1989</cite>,
 *  and Java <a href="http://www.systemics.com/docs/cryptix/">Cryptix</a> code.
 *  <p>
 *
 *  @author Lawrie Brown, Feb 2002
 *  @version Feb 2005
 */

class DES {

    /**
     *  specify whether diagnostic trace output is required.
     *  <p>
     *  Available levels are:<br>
     *  0: no trace info is generated<br>
     *  1: trace major calls with params (setKey, encrypt, decrypt)<br>
     *  2: + trace round values whilst en/decrypting<br>
     *  3: + trace all 14 steps within each round<br>
     *  4: + trace subkey generation<br>
     */
    public int traceLevel = 0;
    /**
     *  string which accumulates diagnostic output for display.
     *  <p>
     *  Contents are reset on each major call (setKey, encrypt, decrypt)
     *  and should be used after each of these calls returns for display.
     */
    public String traceInfo = "";

    /** DES constants and variables. */
    public static final int
        ROUNDS = 8,			// DES has 8 rounds + output
        BLOCK_SIZE = 8,			// DES uses 64-bit (8 byte) data
        KEY_LENGTH = 8,			// DES uses 64-bit (8 byte) key
	NUM_SUBKEYS = 16;		// DES has 16 rounds and hence subkeys

    /** DES key schedule.
     *  Stored as an array of 16 round subkeys by 8 S-box input values
     */
    private byte[][] subkeys = new byte[NUM_SUBKEYS][8];

    //......................................................................
    // Definitions of the fixed DES permutation and substitution tables 
    //	 see Schneier 12.2 for table definitions and explanations

    /** initial permutation IP. */
    private static final byte[] IP = {
	58, 50, 42, 34, 26, 18, 10,  2,
	60, 52, 44, 36, 28, 20, 12,  4,
	62, 54, 46, 38, 30, 22, 14,  6,
	64, 56, 48, 40, 32, 24, 16,  8,
	57, 49, 41, 33, 25, 17,  9,  1,
	59, 51, 43, 35, 27, 19, 11,  3,
	61, 53, 45, 37, 29, 21, 13,  5,
	63, 55, 47, 39, 31, 23, 15,  7	};

    /** final permutation FP. */
    private static final byte[] FP = {
	40,  8, 48, 16, 56, 24, 64, 32,
	39,  7, 47, 15, 55, 23, 63, 31,
	38,  6, 46, 14, 54, 22, 62, 30,
	37,  5, 45, 13, 53, 21, 61, 29,
	36,  4, 44, 12, 52, 20, 60, 28,
	35,  3, 43, 11, 51, 19, 59, 27,
	34,  2, 42, 10, 50, 18, 58, 26,
	33,  1, 41,  9, 49, 17, 57, 25	};

    /** PC1 key schedule initial permutation.
     * <p>
     * Rewritten as a 64 bit permutation, where 4 parity bits are permuted
     * to DES bits 29-32 in each 32 bit int, and subsequently ignored.
     */
    private static final byte[] PC1 = {	
        57, 49, 41, 33, 25, 17,  9,
	 1, 58, 50, 42, 34, 26, 18,
	10,  2, 59, 51, 43, 35, 27,
	19, 11,  3, 60, 52, 44, 36,
	 8, 16, 24, 32,			// <- these are parity bits to ignore
	63, 55, 47, 39, 31, 23, 15,
	 7, 62, 54, 46, 38, 30, 22,
	14,  6, 61, 53, 45, 37, 29,
	21, 13,  5, 28, 20, 12,  4,
	40, 48, 56, 64};		// <- these are parity bits to ignore

    /**  key rotation schedule. */
    private static final byte[] keyrot =
	//1  2  3  4  5  6  7  8  9 10 11 12 13 14 15 16 <- round number
        { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 }; 

    /** PC2 key schedule round permutation. */
    private static final byte[] PC2 = {
    	14, 17, 11, 24,  1,  5,
	 3, 28, 15,  6, 21, 10,
	23, 19, 12,  4, 26,  8,
	16,  7, 27, 20, 13,  2,
	41, 52, 31, 37, 47, 55,
	30, 40, 51, 45, 33, 48,
	44, 49, 39, 56, 34, 53,
	46, 42, 50, 36, 29, 32	};

    /** expansion permutation - not used, implemented by expand(). */
    private static final byte[] E = {
        32,  1,  2,  3,  4,  5,
	 4,  5,  6,  7,  8,  9,
	 8,  9, 10, 11, 12, 13,
	12, 13, 14, 15, 16, 17,
	16, 17, 18, 19, 20, 21,
	20, 21, 22, 23, 24, 25,
	24, 25, 26, 27, 28, 29,
	28, 29, 30, 31, 32,  1  };

    /** DES S-boxes - 48->32 bit substitution tables for each of the 8 boxes.
     * <p>
     * nb: DES bits (16) select row in each table
     *     DES bits (2345) select column in row
     */
    private static final byte[][] Sbox = {
        // S[1]
	{ 14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7,
	 0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8,
	 4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0,
	15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13 },
        // S[2]
	{ 15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10,
	 3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5,
	 0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15,
	13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9 },
        // S[3]
	{ 10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8,
	13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1,
	13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7,
	 1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12 },
        // S[4]
	{  7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15,
	13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9,
	10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4,
	 3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14 },
        // S[5]
	{  2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9,
	14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6,
	 4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14,
	11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3 },
        // S[6]
	{ 12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11,
	10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8,
	 9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6,
	 4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13 },
        // S[7]
	{  4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1,
	13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6,
	 1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2,
	 6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12 },
        // S[8]
	{ 13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7,
	 1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2,
	 7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8,
	 2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11 }
    };

    /** permutation P. */
    private static final byte[] P = {	
        16,  7, 20, 21,
	29, 12, 28, 17,
	 1, 15, 23, 26,
	 5, 18, 31, 10,
	 2,  8, 24, 14,
	32, 27,  3,  9,
	19, 13, 30,  6,
	22, 11,  4, 25	};


    //......................................................................
    /** Construct DES object. */
    public DES() {
    }


    //......................................................................
    /**
     * DES encrypt 64-bit plaintext using key previously set.
     *
     * <p> DES encryption is IP, 16 rounds using f, FP with subkeys in order
     *
     * @param plain the 64-bit plaintext value to encrypt.
     * @return the encrypted 64-bit ciphertext value.
     */
    public byte[] encrypt(byte[] plain) {
	int t;					/* result of func f in round */

        if ((plain == null) || (plain.length != BLOCK_SIZE))
            return (plain);

	if (traceLevel > 0) traceInfo = "encryptDES("+Util.toHEX1(plain)+")";
	int[] data = Util.byte2int(plain);	/* convert to 32-bit ints    */

        data = perm64(data, IP);                /* Apply IP to input block   */
	if (traceLevel > 1)
	    traceInfo += "\n  IP:\tL0="+Util.toHEX1(data[0])+
			 ", R0="+Util.toHEX1(data[1]);

        for (int i=0; i<16; i++) {             /* Compute 16 rounds on data */
	    if (traceLevel > 1)
	        traceInfo += "\n  Rnd"+(i+1)+
			     "\tf(R"+i+"="+Util.toHEX1(data[1])+
		             ", SK"+(i+1)+"="+Util.toHEX(subkeys[i])+") ";
            t = f(data[1], subkeys[i]);		// Compute round fn
	    if (traceLevel > 1) traceInfo += "= "+Util.toHEX1(t);
            t = data[0] ^ t;			// and XOR with L
	    data[0] = data[1];			/* and swap Li = Ri-1        */
	    data[1] = t;			/* Ri = Li-1 XOR f(Ri-1, Ki) */
	}
        t = data[0]; data[0] = data[1]; data[1] = t; /* Unswap final round   */
        data = perm64(data, FP);                /* Perform Inverse IP        */
	if (traceLevel > 1)
	    traceInfo += "\n  FP:\tL="+Util.toHEX1(data[0])+
			 ", R="+Util.toHEX1(data[1]);

        byte[] res = Util.int2byte(data);	/* result back to bytes */
	if (traceLevel > 1) traceInfo += "\n";
	if (traceLevel > 0) traceInfo += " returns "+Util.toHEX1(res)+"\n";
	return (res);
    }

    //......................................................................
    /**
     * DES decrypt 64-bit ciphertext using key previously set.
     *
     * <p> DES decryption is IP, 16 rounds using f, FP with subkeys in reverse
     *
     * @param cipher the 64-bit ciphertext value to decrypt.
     * @return the decrypted 64-bit plaintext value.
     */
    public byte[] decrypt(byte[] cipher) {
	int t;					/* result of func f in round */

        if ((cipher == null) || (cipher.length != BLOCK_SIZE))
            return (cipher);

	if (traceLevel > 0) traceInfo = "decryptDES(" + Util.toHEX1(cipher) + ")";
	int[] data = Util.byte2int(cipher);	/* convert to 32-bit ints    */

        data = perm64(data, IP);                /* Apply IP to input block   */
	if (traceLevel > 1)
	    traceInfo += "\n  IP:\tL0="+Util.toHEX1(data[0])+
			 ", R0="+Util.toHEX1(data[1]);

        for (int i=0; i<16; i++) {             /* Compute 16 rounds on data */
	    if (traceLevel > 1)
	        traceInfo += "\n  Rnd"+(i+1)+
			     "\tf(R"+i+"="+Util.toHEX1(data[1])+
		             ", SK"+(16-i)+"="+Util.toHEX(subkeys[15-i])+") ";
            t = f(data[1], subkeys[15-i]);		// Compute round fn
	    if (traceLevel > 1) traceInfo += "= "+Util.toHEX1(t);
            t = data[0] ^ t;			// and XOR with L
	    data[0] = data[1];			/* and swap Li = Ri-1        */
	    data[1] = t;			/* Ri = Li-1 XOR f(Ri-1, Ki) */
	}
        t = data[0]; data[0] = data[1]; data[1] = t; /* Unswap final round   */
        data = perm64(data, FP);                /* Perform Inverse IP        */
	if (traceLevel > 1)
	    traceInfo += "\n  FP:\tL="+Util.toHEX1(data[0])+
			 ", R="+Util.toHEX1(data[1]);

        byte[] res = Util.int2byte(data);	/* result back to bytes */
	if (traceLevel > 1) traceInfo += "\n";
	if (traceLevel > 0) traceInfo += " returns " + Util.toHEX1(res) + "\n";
	return (res);
    }

    //......................................................................
    /**
     * takes the 64-bit key to use for subsequent en/decryptions,
     * and expands it into the DES subkey schedules.
     *
     * <p>
     * The key is passed as a 64-bit value, of which the 56 non
     * parity bits are used. The parity bits are in DES bits
     * 8, 16, 24, 32, 40, 48, 56, 64 (nb: these do NOT correspond to
     * the parity bits in ascii chars when packed in the usual way).
     * The function performs the key scheduling calculation, saving
     * the resulting sixteen 48-bit sub-keys for use in subsequent
     * encryption/decryption calculations. These 48-bit values are
     * saved as eight 6-bit values, as detailed previously.
     *
     * <p>
     * The key scheduling calculation involves
     * permuting the input key by PC1 which selects 56-bits
     * dividing the 56-bit value into two halves C, D sixteen times 
     * rotates each half left by 1 or 2 places according to schedule in keyrot
     * concatenates the two 28-bit values, and permutes with PC2
     * which selects 48-bits to become the subkey
     *
     * <p>
     * nb: the two 28-bit halves are stored in two ints, with bits <br>
     *    numbered as [1 2 ... 27 28 x x x x] ... [29 30 ... 48 x x x x]<br>
     *    in ints              n[0]               n[1]    (MSB to LSB)<br>
     *    the bottom 4 bits in each longword are ignored<br>
     *    This scheme is used in keyinit(), keysched(), and rotl28()<br>
     *
     * <p>
     *
     * @param  key  the user-key to use.
     */
    public void setKey(byte[] key) {
	final int MASK28 = 0xFFFFFFF0;	// Mask for DES key bits 1 to 28

        if ((key == null) || (key.length != KEY_LENGTH))
            return;

	// create trace info if needed
	if (traceLevel > 0) traceInfo = "setKey("+Util.toHEX1(key)+")\n";

        int[]	cd = Util.byte2int(key);	// convert key to int array

	cd = perm64(cd, PC1);			// Permute key with PC1
        cd[0] &= MASK28;	// form 28-bit value C dropping 4 parity bits
        cd[1] &= MASK28;	// form 28-bit value D dropping 4 parity bits
	if (traceLevel > 3) traceInfo += "  PC1(Key)="+Util.toHEX(cd)+"\n";

        for (int i=0; i<16; i++) {	// gen subkeys for all 16 rounds
            cd[0] = rotl28(cd[0], keyrot[i]);	// rotate CD by 1 or 2 bits
            cd[1] = rotl28(cd[1], keyrot[i]);	//   according to schedule
            keyperm(cd, i);         		// apply PC2 to form SKi

	    // create trace info if needed
	    if (traceLevel > 3)
	        traceInfo += "  KeyRnd"+(i+1)+"\tCD="+Util.toHEX(cd)+
                          "\tPC2(CD)="+Util.toHEX(subkeys[i])+"\n";
        }
    }


    //......................................................................
    /**
     * the complex non-linear DES round function.
     * <p>
     * Its output is a complex function of both input data and sub-key
     * The input data R(i-1) is expanded to 48-bits via expansion fn E,
     * is XOR'd with the subkey K(i), substituted into the S-boxes,
     * and finally permuted by P. ie the calculation is:
     *     A = E(R(i-1)) XOR K(i)          (a 48-bit value)
     *     B = S(A)                        (a 32-bit value)
     *     f = P(B)                        (a 32-bit value)
     *
     * nb: the 48-bit values are stored as eight 6-bit values. 
     * In each byte, the bits are numbered  [x x 1 2 3 4 5 6]
     * Overall the bit numbering is:
     * [x x 1 2 3 4 5 6] [x x 7 8 9 10 11 12] ... [x x 43 44 45 46 47 48]
     * nb: the 6-bit S-box input value [x x 1 2 3 4 5 6] is interpreted as:
     * bits [1 6] select a row within each box,
     * bits [2 3 4 5] then select a column within that row
     * hence the input value is reordered to [x x 1 6 2 3 4 5] before
     * indexing into the S-box tables.
     * <p>
     * There are 14 main events in each of 8 rounds of DES using 6 sub-keys,
     * plus an output transformation using 4 subkeys. 
     *
     * @param R the 32-bit R data value
     * @param SK  the subkey for this round
     * @return result is P(S(E(R)^SK))
     */
    private int f(int R, byte[] SK)
    {
        int     b = 0;			// 32 bit S-box output block
        int     out = 0;		// 32-bit output value
        byte[]	a;			// store E(R) as eight 6-bit values
	int[]	e = new int[8];		// expansion results for trace
	byte[]	s = new byte[8];	// S-box outputs
        int    rc;     			// S-box row-col index

        a = expand(R);			//  expand input Ri to 48 bits 

        for (int j=0; j<8; j++) {	// Lookup S-boxes to get B = S(A)
                rc = (a[j] ^ SK[j]) & 0xFF;	// A = E(R(i-1)) XOR K(i)
		e[j] = rc;			// save expanded value for trace
                rc = (rc & 0x20)  |		// reorder S-box index so
                     ((rc << 4) & 0x10) |	//   bits 1,6 form the row
                     ((rc >>> 1) & 0x0F);	//   bits 2-5 form the col
                s[j] = Sbox[j][rc];		// S-box j output
                b = (b << 4) | (s[j] & 0x3F);	// Concatenate S-box output to b
        }

        out = perm32(b, P);		// Apply 32-bit permutation P to B

	if (traceLevel > 2) 
	    traceInfo += "\n\tE="+Util.toHEX(a)+"  S="+Util.toHEX(s)+"  P";

        return(out);
    }

    //......................................................................
    // Assorted utility routines for DES computations

    /** compute the PC2 key round permutation.
     *  takes the two key halves in CD, permutes them according to PC2,
     *  and generates a 48-bit output value stored as eight 6-bit (byte) values
     *
     * @param cd the two 28-bit (int) C and D key schedule halves
     * @param i  the round number
     */
    private void keyperm(int[] cd, int i) {
        byte	KEYBIT1 = 0x20;			// bit 1 of a 6-bit value
        byte	mask;				// mask used to set output bit
	int	p = 0;				// index into PC2 array

        // Process both C and D halves
        for (int j=0; j<8; j++) {		// For each S-box input (S1-8)
            subkeys[i][j] = 0;			// Clear output word
            mask = KEYBIT1;			// Reset mask to bit 1
            for (int k=0; k<6; k++) {		// For each S-box input bit 1-6
                if (keybit(cd, PC2[p++]) == 1)  // If input bit permuted
                        subkeys[i][j] |= mask;	// to this loc is 1, set it
                mask >>>= 1;			// Shift mask to next bit
            }
        }
    }

    /** returns the bit of key at position pos in n (1 <= pos <= 56).
     *  <p>Where n is the two 24-bit halves used in the key scheduling, and
     *  bits are numbered [1 2 ... 27 28 x x x x]  [29 30 ... 56 x x x x]<br>
     *  in ints             n[0]                       n[1]<br>
     *  nb: bottom 4 bits in each int are ignored
     *
     * @param cd the two 28-bit (int) C and D key schedule halves
     * @param pos bit position to extract from CD
     * @return wanted bit at position pos in CD
     */
    private int keybit(int[] cd, int pos) {
        int d = (pos - 1) / 28;			// calculate half bit is in
        int o = 31 - ((pos - 1) % 28);		// calculate offset within half
        int b = ((cd[d] >>> o) & 01);		// shift this bit to LSB & mask
        return (b);				// and return wanted bit
    }
    
    /** rotates int value b (where b is a 28-bit key half)
     *  left by pos bits, (0 <= pos <= 28).
     *
     * @param b a 28-bit (int) key half from CD
     * @param n number of bits to rotate b left by
     */
    private int rotl28(int b, int n) {
	final int MASK28 = 0xFFFFFFF0;	// Mask for DES key bits 1 to 28
        return (((b << n) | (b >>> (28-n)) ) & MASK28);
    }

    //......................................................................
    // Compute various data permutations

    /** expands the 32 bit value R into a 48-bit value A.
     *  <p>
     *  Result is stored as eight 6-bit values in a byte array,
     *  with bits numbered as specified previously. 
     *  <p>
     *  Due to the regular nature of the expansion matrix E,
     *  this function implements it directly for efficiency reasons
     *  It takes each 4-bit nybble of the input word, and concatenates
     *  it with the adjacent bit on either side to form a 6-bit value.
     *  (nb: bit 32 is assumed to be adjacent to bit 1)
     */
    private byte[] expand(int R) {
        final int MASK6 = 0x3f;			// mask out all bar lower 6-bits
	byte[]	a = new byte[8];		// array to store result in
        int t;					// temporary storage for values
    
        t = (R<<5)|(R>>>27); a[0] = (byte)(t & MASK6);  // bits 32  1  2  3  4  5
        t = R >>> 23;    a[1] = (byte)(t & MASK6);  // bits  4  5  6  7  8  9
        t = R >>> 19;    a[2] = (byte)(t & MASK6);  // bits  8  9 10 11 12 13
        t = R >>> 15;    a[3] = (byte)(t & MASK6);  // bits 12 13 14 15 16 17
        t = R >>> 11;    a[4] = (byte)(t & MASK6);  // bits 16 17 18 19 20 21
        t = R >>> 07;    a[5] = (byte)(t & MASK6);  // bits 20 21 22 23 24 25
        t = R >>> 03;    a[6] = (byte)(t & MASK6);  // bits 24 25 26 27 28 29
        t = (R<<1)|(R>>>31); a[7] = (byte)(t & MASK6);  // bits 28 29 30 31 32  1
	return(a);
    }
    
    /** general permutation of a 32-bit input block to a 32-bit output block,
     *  under the control of a permutation array perm.
     *  Each element of perm specifies which input bit is to be permuted to
     *  the output bit with the same index as the array element.
     *  <p>
     *  nb: to set bits in the output word, as mask with a single 1 in it is
     *  used. On each step, the 1 is shifted into the next location
     */
    private int perm32(int in, byte[] perm) {
        final int DESBIT1 = 0x80000000;		// DES bit 1, MSB of 32-bit word
        int mask = DESBIT1;			// mask used to set output bit
	int out  = 0;				// output value
	int off;				// offset to shift bit by
	int p = 0;				// index into perm array
    
        for (int i=0; i<32; i++) {		// For each bit position
	    off = 31 - (perm[p++]-1);		// compute offset to shift by
            if (((in >>> off) & 1) == 1)	// if the input bit permuted
                out |= mask;			//   to this loc is 1, set it
            mask >>>= 1;			// Shift mask to next bit
        }
	return(out);
    }
    
    /** general permutation of a 64-bit input block to a 64-bit output block,
     *  under the control of a permutation array perm.
     *  <p>
     *  Each element of perm specifies which input bit is to be permuted to
     *  the output bit with the same index as the array element.
     *  <p>
     *  nb: to set bits in the output word, a mask with a single 1 in it is
     *  used. On each step, the 1 is shifted into the next location
     */
    private int[] perm64(int[] in, byte[] perm) {
        final int DESBIT1 = 0x80000000;		// DES bit 1, MSB of 32-bit word
        int mask = DESBIT1;			// mask used to set output bit
	int[] out  = new int[2];		// output values
	int p = 0;				// index into perm array
    
        // Process left half out[0]
        out[0] = 0;				// Clear output word
        for (int i=0; i<32; i++) {		// For each bit position
            if (bit(in, perm[p++]) == 1)	// If the input bit permuted
                out[0] |= mask;			// to this loc is 1, set it
            mask >>>= 1;			// Shift mask to next bit
        }

	// Process right half out[1]
        out[1] = 0;				// Clear output word
        mask = DESBIT1;
        for (int i=0; i<32; i++) {		// For each bit position
            if (bit(in, perm[p++]) == 1)	// If the input bit permuted
                out[1] |= mask;			// to this loc is 1, set it
            mask >>>= 1;			// Shift mask to next bit
        }

	return(out);
    }
    

    /** return bit at position pos in data array n (1 <= pos <= 64).
     *  <p>
     *  bits are numbered [1 2 ... 31 32]  [33 34 ... 64]<br>
     *  in ints             n[0]               n[1]<br>
     */
    private int bit(int[] n, int pos) {
        pos--;					// pos now has 0 offset
        int d = pos / 32;			// calculate half bit is in
        int o = 31 - (pos % 32);		// calculate offset within half
        int b = ((n[d] >>> o) & 01);		// shift bit to LSB, mask it
        return (b);				// and return it
    }


    /** self-test routine for DES cipher
     *  @param hkey	key to test in hex
     *  @param hplain	plaintext to test in hex
     *  @param hcipher	ciphertext to test in hex
     *  @param lev	trace level to use
     */
    public static void self_test (String hkey, String hplain, String hcipher, int lev) {

        // convert string triple components to binary
        byte [] key	= Util.hex2byte(hkey);
        byte [] plain	= Util.hex2byte(hplain);
        byte [] cipher	= Util.hex2byte(hcipher);
	byte [] result;

        System.out.println();

	DES testDES = new DES();	// create DES instance to test triple
	testDES.traceLevel = lev;	// select level of trace info 
	testDES.setKey(key);		// set key and display trace info
        System.out.print(testDES.traceInfo);

	result = testDES.encrypt(plain);	// test encryption
        System.out.print(testDES.traceInfo);
	if (Arrays.equals(result, cipher))
	    System.out.print("Test OK\n");
	else
	    System.out.print("Test Failed. Result was "+Util.toHEX(result)+"\n");

	result = testDES.decrypt(cipher);	// test decryption
        System.out.print(testDES.traceInfo);
	if (Arrays.equals(result, plain))
	    System.out.print("Test OK\n");
	else
	    System.out.print("Test Failed. Result was "+Util.toHEX(result)+"\n");
    }

    /** Test harness main to self-test this DES cipher class.
        @param args command line arguments
     */
    public static void main (String[] args) {
	int lev = 2;

	// process command-line variants
	switch (args.length) {
	  case 0:	break;
	  case 1:	lev = Integer.parseInt(args[0]);
	  		break;
	  case 3:	self_test(args[0], args[1], args[2], lev);
	  		System.exit(0);
	  		break;
	  case 4:	lev = Integer.parseInt(args[3]);
	  		self_test(args[0], args[1], args[2], lev);
	  		System.exit(0);
	  		break;
	  default:	System.out.println("Usage: DES [lev | key plain cipher {lev}]\n");
	  		System.exit(1);
	}

        // DES test triple from Katzan
        self_test("5B5A57676A56676E", "675A69675E5A6B5A",
		  "974AFFBF86022D1F", lev);
    }
}

