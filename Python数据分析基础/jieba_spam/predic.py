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
    strang_digit = '‖◇⊙ü＆〕〔○℃Ｈ」＋〈≤ＩＱ★━●→\\…２４１０６８７６３９－◆【】■％？！（）：《》·_——～“”!／#$%^&*[]{}./,`~;|'
    email_count = 0
    file_count = 0
    strang_char=0
    P = 0
    for segl in file_seg_list:
        file_count += 1
        print(str, file_count, ":", P,P > 75)
        P = 0
        #strang_char=0
        for word_list in segl:
            for word in word_list:
                if word in result:
                    P += result[word]
        #        if word in strang_digit:
        #            strang_char+=1
        avg_P+=P
        if(P > 75 ):#or strang_char > 28):
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
