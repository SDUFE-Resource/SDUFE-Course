def encrypt(plain,key):
    grid=[['\n' for i in range(len(key))]for j in range(int(len(plain)/len(key))+1)]
    row,col=0,0
    for i in range(len(plain)):
        if col==len(key):
            row+=1
            col=0
        shift=key[col]
        grid[row][col+shift]=plain[i]
        col+=1
    cip=[]
    for i in range(int(len(plain)/len(key))+1):
        for j in range(len(key)):
            cip.append(grid[i][j])
    print("".join(cip))

def decrypt(cipher,key):
    grid=[['\n' for i in range(len(key))]for j in range(int(len(cipher)/len(key))+1)]
    row,col=0,0
    for i in range(len(key)):
        key[i]=-key[i]
    for i in range(len(cipher)):
        if col==len(key):
            row+=1
            col=0
        shift=key[col]
        grid[row][col-shift]=cipher[i]
        col+=1
    pla=[]
    for i in range(int(len(cipher)/len(key))+1):
        for j in range(len(key)):
            pla.append(grid[i][j])
    print("".join(pla))
state=input("please chose the model of Permutation Cipher\nA. encryption\nB. decryption\nEnter A or B to chose:")

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
