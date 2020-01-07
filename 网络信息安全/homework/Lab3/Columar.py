import math

def encrypt(plain,key):
    cip="" #establish a empty string to store ciphertext
    col=len(key) #calcuate the number of column in matrix
    right_k=sorted(key) #get the ordered key
    row=int(math.ceil(len(plain)/len(key))) # get the number of row in matrix
    fill_null=int((row*col)-len(plain)) #calcuate how many empty grid we need
    plain=list(plain) #converse plaintext to list
    plain.extend('_'*fill_null) #insert empty character in the tail of the plaintext list
    matrix = [plain[i:i+col] for i in range(0,len(plain),col)] #fill matrix with character by row-wise
    # read matrix by column-wise
    for i in range(col):
        curr_idx=key.index(right_k[i])
        cip += ''.join(row[curr_idx] for row in matrix)

    print("".join(cip))

def decrypt(cipher,key):
    pla=""
    index=0
    col=len(key)
    right_k=sorted(key)
    row=int(math.ceil(len(cipher)/col))
    cipher=list(cipher)
    matrix=[]
    for i in range(row):
        matrix += [[None]*col]
    for i in range(col):
        curr_idx=key.index(right_k[i])
        for j in range(row):
            matrix[j][curr_idx]=cipher[index]
            index+=1
    pla = ''.join(sum(matrix, []))

    print("".join(pla))

state=input("please chose the model of Cloumnar Transposition Cipher\nA. encryption\nB. decryption\nEnter A or B to chose:")

if state=="A":
    plain=input("\nPlease input the plaintext:")
    key=input("\nPlease input the tuple of key(list format):")
    key=eval(key)
    encrypt(plain,key)
elif state=="B":
    cipher=input("\nPlease input the ciphertext:")
    key=input("\nPlease input the tuple of key(list format):")
    key=eval(key)
    decrypt(cipher,key)
else:
    print("You should select a right model")
