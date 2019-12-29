# encoding=UTF-8
import jieba
import math

path = "/Users/jason/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/delay/"


def cut_list(s):
    file_seg_list = []
    empty = []
    for i in s:
        spam_path = path+i+".txt"
        f = open(spam_path, "r")
        seg_list = []
        for line in f:
            seg_list.append(jieba.cut(line))
        file_seg_list.append(seg_list)
    return file_seg_list


def TF_IDF(fsl, tf):
    file_count = len(fsl)
    tf_idf = {}
    idf = {}
    for key in tf:
        idf[key] = 1
        tf_idf[key] = 0

    for file in fsl:
        for word in file:
            if word in tf:
                idf[word] += 1
    for key, value in idf.items():
        idf[key] = math.log(file_count/value+1)
    for key, value in tf_idf.items():
        tf_idf[key] = tf[key]*idf[key]
    return tf_idf


def TF_cal(fsl):
    dic = {}
    count = 0
    for segl in fsl:
        for word_list in segl:
            count += 1
            #print(count)
            for word in word_list:
                if word in dic:
                    dic[word] += 1
                else:
                    dic[word] = 0
    for key, value in dic.items():
        dic[key] = value/count
    tf = dic
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

file_seg_list = cut_list(s)
word_P = TF_IDF(file_seg_list, TF_cal(file_seg_list))
word_list = sorted(word_P.items(), key=lambda x: x[1], reverse=True)
for i in word_list:
    print(i)
