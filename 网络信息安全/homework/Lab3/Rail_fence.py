def encrypt(plain,key):
    rail=[['\n' for i in range(len(plain))] for j in range(key)]
    dir_floor=False
    row,col=0,0
    for i in range(len(plain)):
        if row==0 or row==key-1:
            dir_floor = not dir_floor
        rail[row][col]=plain[i]
        col=col+1
        if dir_floor:
            row =row+1
        else:
            row =row-1

    cip=[]

    for i in range(key):
        for j in range(len(plain)):
            if rail[i][j]!='\n':
                cip.append(rail[i][j])

    print("".join(cip))

def decrypt(cipher,key):
    rail=[['\n' for i in range(len(cipher))]for j in range(key)]
    dir_down=None
    row,col=0,0
    for i in range(len(cipher)):
        if row==0:
            dir_down=True
        elif row==key-1:
            dir_down=False

        rail[row][col]='*'
        col+=1

        if dir_down:
            row+=1
        else:
            row-=1
    index=0
    for i in range(key):
        for j in range(len(cipher)):
            if(rail[i][j]=='*') and (index<len(cipher)):
                rail[i][j]=cipher[index]
                index+=1
    pla=[]
    row,col=0,0
    for i in range(len(cipher)):
        if row==0:
            dir_down=True
        elif row==key-1:
            dir_down=False

        pla.append(rail[row][col])
        col+=1

        if dir_down:
            row+=1
        else:
            row-=1
    print("".join(pla))

state=input("please chose the model of Rail Fence Cipher\nA. encryption\nB. decryption\nEnter A or B to chose:")

if state=="A":
    plain=input("\nPlease input the plaintext:")
    key=input("\nPlease input the key:")
    key=int(key)
    encrypt(plain,key)
elif state=="B":
    cipher=input("\nPlease input the ciphertext:")
    key=input("\nPlease input the key:")
    key=int(key)
    decrypt(cipher,key)
else:
    print("You should select a right model")
