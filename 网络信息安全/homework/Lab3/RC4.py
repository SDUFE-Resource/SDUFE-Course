import math
def RC4(text,key):
    KSA(key)
    PRGA()
    print()

def KSA(key):
    S=list(range(0,256))
    T=[key[i mod len(key)]]
    print(S)
    return S

def PRGA(): 
    keystream=[]
    return keystream
state=input("please chose the model of RC4 Transposition Cipher\nA. encryption\nB. decryption\nEnter A or B to chose:")

if state=="A":
    plain=input("\nPlease input the plaintext:")
    key=input("\nPlease input the seed:")
    RC4(plain,key)
elif state=="B":
    cipher=input("\nPlease input the ciphertext:")
    key=input("\nPlease input the seed:")
    RC4(cipher,key)
else:
    print("You should select a right model")
