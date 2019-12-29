name=('.txt' '.tmp' '.log' '.md' '.cpp' '.py')

int=1
while(($int <= 50))
do
    let "int++"
    mod=6
    val=`expr $RANDOM % $mod`
    str=$RANDOM
    touch $str${name[$val]}
    dele=`expr $str % 2`
    echo $dele
    write='do not delete this file'
    $(echo $write >> $str${name[$val]})
    if [ dele == 1 ]
    then
        $(echo $write >> $str${name[$val]})
    fi
done
