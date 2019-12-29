import java.io.*;
import java.util.Random;

/** GenDES - main program for random DES triples generator
  
    @author Lawrie Brown <Lawrie.Brown@adfa.edu.au>      Feb 2002
 */
public class GenDES {

    /** DES Cipher object used for calculations. */
    private DES myDES;

    /** usage description for command-line invocation of the DES calculator. */
    private static final String usage = "Usage:\n" +
	"GenDES [n]\n" +
	"	- generate 1 [n] random DES triples\n";

    //......................................................................
    /** ideacalc program main routine.
	@param args command line arguments
     */
    public static void main (String[] args) {
    
	byte[]  key = new byte[DES.KEY_LENGTH];	/* 64-bit DES data */
	byte[]  data = new byte[DES.BLOCK_SIZE]; /* 64-bit DES key */
	byte[]  result;				/* 64-bit DES result */
	DES	testDES;			/* DES cipher object */
	Random	gen;				/* random number generator */
	int	numTriples = 1;			/* no triples to generate */
    
	/* check command-line args */
	if (args.length > 0) {			/* if no args, run as GUI */
	    numTriples = Integer.parseInt(args[0]);
	}

	/* create a new random generator object, seeded with current time */
	gen = new Random(System.currentTimeMillis());

	/* display initial comment */
	System.out.println("# Random DES (key,plain,cipher) triples");

	/* loop for desired number of triples */
	for (int i = 0; i < numTriples; i++) {
	    /* load arrays with random key and data values for this triple */
	    gen.nextBytes(key);
	    gen.nextBytes(data);

	    /* now create DES cipher object and encrypt data to get cipher */
	    testDES = new DES();
	    testDES.setKey(key);
	    result = testDES.encrypt(data);

	    /* and display the resulting triple */
	    System.out.println( Util.toHEX1(key) + " " +
		Util.toHEX1(data)  + " " + Util.toHEX1(result) );
        }
    }
}

