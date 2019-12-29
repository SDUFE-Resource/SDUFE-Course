### 实验内容和要求
### 1.读写文本文件并添加行号
编写一个程序 `demo.py`，程序运行后，生成新的程序文件 `demo_new.py`，其中程序内容与 `demo.py` 一致，只是在每一行的后面增添单行注释，注释内容为该行的行号，并且注释开始符号#在垂直方向上对齐。
```python
import os

file = open('./' + 'demo_new' + '.py', 'w')
org_file = open('./demo.py')
count = 0
pos = 30
max = 0
for line in org_file:
    if(len(line) > max):
        max = len(line)
max += 1
print(max)

org_file = open('./demo.py')
for line in org_file:
    count += 1
    line = line[0:len(line)-1]
    num_space = max-len(line)
    str1 = ""
    for i in range(0, num_space):
        str1 += " "
    file.write(line+str1+"#"+str(count)+"\n")
```
### 2.磁盘垃圾文件清理
编写函数，接收文件夹作为参数，删除文件夹下所有(含子目录)类型为 tmp、log、txt 以及大小为零的文件。构造目录及相关文件测试函数功能。
```python
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
```
