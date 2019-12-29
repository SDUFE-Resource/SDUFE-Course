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
    chardigit = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789‖◇⊙ü＆〕〔○℃Ｈ」＋〈≤ＩＱ★━●→\t\\…２４１０６８７６３９－◆【】■％？！（）。：，《》·_——、～“；”!@／? #$%^&*()[]{}./,`~;:|<>-=+\"\"\n'
    file_w = open(spam_path, 'w')
    for line1 in lines:
        sts = ''
        for ch in line1:
            if ch not in chardigit:
                sts = sts+ch
        print(sts)
        file_w.write(sts)
    file_w.close()
