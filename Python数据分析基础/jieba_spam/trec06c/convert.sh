for dir in `ls ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data`; do
    for file in `ls ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data/$dir`; do
	cd ~/Documents/Note/Python_Data_Analysis_Fundamental/jieba_spam/trec06c/data/$dir/    
	iconv -f GB2312 -t UTF-8 $file > $file'.txt'
    rm -rf $file
done
done
