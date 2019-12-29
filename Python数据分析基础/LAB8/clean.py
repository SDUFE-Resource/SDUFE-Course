import os
import string

path="/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/LAB8"

dir=os.listdir(path)

for file in dir:
    result=0
    str=os.path.basename(path+"/"+file)
    result+=str.find('.txt')
    result+=str.find('.log')
    result+=str.find('.tmp')
    size=os.path.getsize(path+"/"+file)
    if result is not -3 or size is 0:
        os.remove(path+"/"+file)
