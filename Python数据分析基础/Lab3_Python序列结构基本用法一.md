### Python 序列结构基本用法(一)
#### 列表的使用
#### 列表推导式
#### 综合应用
1. 分别使用 for 循环(自学第四章相关内容)和列表推导式编写程序,判定输入的自然数是不是素数。(提示:素数判定办法:对于给定的自然数 x>2,如果 x 不能被 2~√x 之间的整数所整除,则 x 是素数)
  + for loop
```python
import math
def junge(x):
    flag=0
    for i in range(2,int(math.sqrt(x))+1):
        if(x%i==0):
            flag=1
            break
    if(flag):
        print("It isn't a prime number.\n")
    else:
        print("It is a prime number.\n")


x=input("Please input a normal number:")
x=int(x)
junge(x)
```
  + list
```python
import math
def junge(x):
    flag=0
    num=[x % i for i in range(2,int(math.sqrt(x))+1)]
    print(num)
    if 0 in num:
        print("It isn't a prime number.\n")
    else:
        print("It is a prime number.\n")


x=input("Please input a normal number:")
x=int(x)
junge(x)
```
2. 编写程序,输入一个字符串,输出其中出现次数最多的字符及其出现的次数。使用字典。
```python
x=input("Please input a string:")
dict={}
for i in x:
    if i in dict:
        dict[i]=dict[i]+1
    else:
        dict[i]=1
flag=0
for i in dict:
    if(dict[i]>flag):
        flag=dict[i]

for i in dict:
    if(dict[i] is flag):
        print(i)
```
