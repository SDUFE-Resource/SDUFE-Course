def decrypt(text,s):
	result = ""
	for i in range(len(text)):
		char = text[i]
		if (char.isupper()):
		    result += chr((ord(char) + s-65) % 26 + 65)
		elif (char.islower()):
		    result += chr((ord(char) + s - 97) % 26 + 97)
		else :
			result += chr(ord(char) + 0)

	return result

def find():
    flag=1
    i=0
    while(flag==1):
        s = ord('e')-ord(fre[i])
        print ("\n",decrypt(text,s))
        f=input("It's the right sentence? Please input y or n\n")
        if(f=='y'):
            flag=0
        elif(f=='n'):
            flag=1
            i=i+1
        else:
            flag=0
            print("It's not correct input, please try again.")
    
text = input("Please input the infromation you want to decrypt:\n")
x= text
dict={}

for i in x:
    if(i.islower() or i.isupper()):
        if i in dict:
            dict[i]=dict[i]+1
        else:
            dict[i]=1

fre=sorted(dict, key=dict.__getitem__,reverse=True)
#print(sorted(dict, key=dict.__getitem__,reverse=True))
find()

