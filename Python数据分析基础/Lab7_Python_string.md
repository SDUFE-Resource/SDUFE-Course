### 实验内容和要求
### 1.验证 6174 猜想
1955 年,卡普耶尔对 4 位数字进行研究,发现了一个规律:对任意各位数字不相同的四位数,使用该数各位数字能组成的最大数减去能组成的最小数,对得到的差重复进行以上操作,最终总能得到 6174 这个数字,并且操作过程不超过 7 次。

编写程序,使用枚举法(即对所有符合上述条件的四位数字)对这个过程进行验证,并打印每个数对应的操作次数。

提示:permutations 函数使用方法:permutations (iterable, r)该函数接受两个参数,iterable 为一个可迭代的序列结构,r 为整数,函数返回值为 iterable 结构中 r 个元素的排列序列。
```python
import itertools as it
import math


def list2num(list):
    value = ""
    value += str(list[0])
    value += str(list[1])
    value += str(list[2])
    value += str(list[3])
    return int(value)


def max_minus_min(inputlist, ifprint, count):
    correct_num = list2num(inputlist)
    Biggest_num = list(inputlist)
    Smallest_num = list(inputlist)
    Biggest_num.sort(reverse=True)
    Smallest_num.sort()
    Biggest = list2num(Biggest_num)
    Smallest = list2num(Smallest_num)
    number = Biggest-Smallest
    if ifprint is 1:
        print("Present Biggest Smallest")
        print(str(correct_num)+"    "+str(Biggest)+"    "+str(Smallest))
    else:
        print(str(Biggest)+"-"+str(Smallest)+"="
              + str(number)+"  Count:"+str(count))
    return number


numbers = [1, 2, 3, 4, 5, 6, 7, 8, 9, 0]
permut = list(it.permutations(numbers, 4))
flag = 0
for i in permut:
    if(i[0] == 0):
        continue
    else:
        index = list2num(i)
        count = 0
        index_list = []
        while(index != 6174):
            if count is 0:
                minus = max_minus_min(i, 1, count)
            else:
                minus = max_minus_min(index_list, 0, count)
            index = minus
            index_list = []
            index_list.append(int(math.floor(minus/1000)))
            index_list.append(int(math.floor(minus % 1000/100)))
            index_list.append(int(math.floor(minus % 100/10)))
            index_list.append(int(math.floor(minus % 10)))
            count += 1
            if(count > 7):
                flag = 1
if flag is 1:
    print("This conjecture is wrong")

```



### 2. 简单垃圾邮件识别 

### a)  利用文本模拟垃圾邮件内容。设定常见垃圾邮件特征词汇

如“发票”、“购买”、“征稿”、“促销”等，并设定每个特征值对应的垃圾邮件概率 pi，利用公式 $P=1-(1-p1)*(1-p2)*......*(1-pi)$ 来计算邮件是正常邮件的概率，设定阈值，对垃圾邮件进行判定。尽量提高准确度， 减少漏判和误判。 



#### 思路

为了设置一个比较符合实际的关键词概率，需要从真实数据集中提取关键词并按其出现的概率，假设数据集中的文件总数为$F_N$，某关键词出现过的文件个数是$F_n$，所以某个关键词对应的概率可以表示为 $p_i=\frac{F_n}{F_N}$。

首先对数据集进行清洗，通过下面这个样例可以发现每一个文件都包含了两个部分：邮件收发信息和文本内容。而且数据集的编码为`GB2312`需要转换，数据集中包含一些无法打开的文件需要删除。这里通过一个shell脚本来批量转化文件编码并标记不可用的文件。

+ /trec06/convert.sh

```shell
for dir in `ls ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data`; do
    for file in `ls ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data/$dir`; do
	cd ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data/$dir/    
	iconv -f GB2312 -t UTF-8 $file > $file'.txt'
	rm -rf $file
done
done
```

现在我们得到了邮件的`UTF-8`版本，下一步是去除邮件中无用的英文信息，如收发信息，超链接和网址等。

+ /trec06/wash_data.py

```python
# coding:UTF-8
path = "/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/delay/"
s = []
f = open(path+"/index")
iter_f = iter(f)
for line in iter_f:
    if(line[0] == 'H' or line[1] == 'P'):
        continue
    else:
        s.append(line[5:20])

for i in s:
    spam_path = path+i+".txt"
    f = open(spam_path, 'r')
    lines = f.readlines()
    f.close()
    chardigit = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789★━●→\\…２４１６８７６３９－◆\t【】■％？！（）。：，《》·_——、～“；”!@／? #$%^&*()[]{}./,`~;:|<>-=+\"\"\n'
    file_w = open(spam_path, 'w')
    for line1 in lines:
        sts = ''
        for ch in line1:
            if ch not in chardigit:
                sts = sts+ch
        print(sts)
        file_w.write(sts)
    file_w.close()
```

经过清洗的邮件就变为了一个只包含中文字符的字符串，这样我们就可以使用jieba对其分词，并通过tf-idf方法获得垃圾文件的关键词。

+ /jieba_spam/tf-idf.py

```python
# encoding=UTF-8
import jieba
import math

path = "/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/delay/"

def cut_list(s):
    file_seg_list=[]
    empty=[]
    for i in s:
        spam_path = path+i+".txt"
        f = open(spam_path, "r")
        seg_list=[]
        for line in f:
            seg_list.append(jieba.cut(line))
        file_seg_list.append(seg_list)
    return file_seg_list

def TF_IDF(fsl,tf):
    file_count=len(fsl)
    tf_idf={}
    idf={}
    for key in tf:
        idf[key]=1
        tf_idf[key]=0

    for file in fsl:
        for word in file:
            if word in tf:
                idf[word]+=1
    for key,value in idf.items():
        idf[key]=math.log(file_count/value+1)
    for key,value in tf_idf.items():
        tf_idf[key]=tf[key]*idf[key]
    return tf_idf

def TF_cal(fsl):
    dic={}
    count=0
    for segl in fsl:
        for word_list in segl:
            count+=1
            print(count)
            for word in word_list:
                if word in dic:
                    dic[word]+=1
                else:
                    dic[word]=0
    for key,value in dic.items():
        dic[key]=value/count
    tf=dic
    return tf

s = []
f = open(path+"/index")
iter_f = iter(f)
str1 = ""

for line in iter_f:
    if(line[0] == 'H' or line[1] == 'P'):
        continue
    else:
        s.append(line[5:20])

file_seg_list=cut_list(s)
word_P=TF_IDF(file_seg_list, TF_cal(file_seg_list))
word_list=sorted(word_P.items(), key=lambda x: x[1], reverse=True)
for i in word_list:
    print(i)
```

这里按照tf-idf值从上向下选词，注意去除一些普通词汇，大概有54,000个关键词。删除权重小于0.01的关键词，还剩下10,000个左右。

```bash
$ python3 tf_idf.py > word.txt
```

得到概率时候可以根据jieba分词对新的文件进行预测。

+ /jieba_spam/predict.py

```python
# encoding=UTF-8
import tf_idf


def check(str, result):
    path = "/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/delay/"
    f = open(path+"/index")
    iter_f = iter(f)
    s = []
    avg_P=0
    if str == 'H':
        plus = 0
    else:
        plus = 1
    for line in iter_f:
        if(line[0] == str):
            print(line)
            s.append(line[4+plus:19+plus])
        else:
            continue

    file_seg_list = tf_idf.cut_list(s)

    email_count = 0
    file_count = 0
    P = 0
    for segl in file_seg_list:
        file_count += 1
        print(str, file_count, ":", P,P > 75)
        P = 75
        for word_list in segl:
            for word in word_list:
                if word in result:
                    P += result[word]
        avg_P+=P
        if(P > 70):
            email_count += 1
    print("NOTICE!!!!!!!!!:",avg_P/len(file_seg_list))
    print(email_count/len(file_seg_list))
    return email_count/len(file_seg_list)


wordpath = "/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/word.txt"

file = open(wordpath)
result = {}
for line in file:
    line = line.strip()
    result[line.split(',')[0]] = line.split(',')[1]
for key, value in result.items():
    result[key] = float(result[key])
file.close()

ham = 1-check('H', result)
spam = check('S', result)

print("Check:")
print("Ham:", ham)
print("Spam:", spam)
```

 得到结果：

```
Check:
Ham: 0.8585408435174124
Spam: 0.6224156438138797
```



数据集地址：https://plg.uwaterloo.ca/~gvcormac/treccorpus06/

###  b) 反干扰措施

垃圾邮件发送者往往会在邮件中插入一些特殊符号如 【、】、-、*、/等，干扰对邮件特征词的提取，但一般邮件中以上 特殊符号出现的总量应该不会太多，如果此类符号出现的总量超过 一定阈值(自己设定)，不需要进行 a)步骤的判断，即可直接判定 为垃圾邮件。 

编写函数，实现垃圾邮件判定器，并构造数据进行验证。 

这里我们只需要在predict中加一行代码就好了，太简单了，就不说了。

#### Reference

+ Wiki:tf-idf:https://zh.wikipedia.org/wiki/Tf-idf