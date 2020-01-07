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


text = input("Please input the infromation you want to decrypt:\n")

for s in range(1,26):
    print (s,": ", decrypt(text,s))
