### Python 序列结构基本用法(二)
#### Eratosthenes
1. 使用列表完成以上操作,返回一个包含小于 N 的所有素数的列表。
```python
#Eratosthenes list
import math
n=input('Please input a number:')
n=int(n)
list=[]
corr_idx=0
for i in range(2,n):
    list.append(i)
index=list[corr_idx]
while(index<math.sqrt(n)):
    flag=index
    index+=flag
    #print(index,index in list)
    while(index<=n):
        if(index in list):
            list.remove(index)
        index+=flag
    corr_idx+=1
    index=list[corr_idx]

print(list)
```
2. 使用集合完成以上操作,返回一个包含小于 N 的所有素数的集合。
```python
#Eratosthenes set
import math
n=input('Please input a number:')
n=int(n)
set={0}
prime={0}

for i in range(2,n):
    set.add(i)
set.remove(0)
index=set.pop()
prime.add(index)
prime.remove(0)
while(index<math.sqrt(n)):
    flag=index
    index+=flag
    while(index<=n):
        if(index in set):
            set.remove(index)
        index+=flag
    index=set.pop()
    prime.add(index)

print(sorted(prime.union(set)))
```
