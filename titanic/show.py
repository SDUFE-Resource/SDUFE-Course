import pandas as pd #数据分析
import numpy as np #科学计算
from pandas import Series,DataFrame

data_train = pd.read_csv('/home/jason/Documents/ML/titanic/train.csv',engine = 'python',encoding='UTF-8')
data_train #dataframe格式

 
import matplotlib.pyplot as plt
fig = plt.figure()
fig.set(alpha=0.2) #设定图表颜色颜色

plt.subplot2grid((2,3),(0,0)) #分出小图
data_train.Survived.value_counts().plot(kind='bar') #柱状图
plt.title(u"Surivied(=1)") #标题
plt.ylabel(u"number")

plt.subplot2grid((2,3),(0,1))
data_train.Pclass.value_counts().plot(kind="bar")
plt.ylabel(u"number") #人数
plt.title(u"the Pclass")

plt.subplot2grid((2,3),(0,2))
plt.scatter(data_train.Survived, data_train.Age)
plt.ylabel(u"Age") #设定纵坐标名称
plt.grid(b=True, which='major', axis='y')
plt.title(u"Survived by age(=1)")

plt.subplot2grid((2,3),(1,0),colspan=2)
data_train.Age[data_train.Pclass == 1].plot(kind='kde')   
data_train.Age[data_train.Pclass == 2].plot(kind='kde')
data_train.Age[data_train.Pclass == 3].plot(kind='kde')
plt.xlabel(u"Age")# plots an axis lable
plt.ylabel(u"Density") #密度
plt.title(u"The age of each pclass")
plt.legend((u'level1', u'level2',u'level3'),loc='best') # sets our legend for our graph.


plt.subplot2grid((2,3),(1,2))
data_train.Embarked.value_counts().plot(kind='bar')
plt.title(u"the number of each embarked")
plt.ylabel(u"number")  

fig = plt.figure()
fig.set(alpha=0.2)  # 设定图表颜色alpha参数

fig = plt.figure()
fig.set(alpha=0.2)  # 设定图表颜色alpha参数

Survived_0 = data_train.Pclass[data_train.Survived == 0].value_counts()
Survived_1 = data_train.Pclass[data_train.Survived == 1].value_counts()
df=pd.DataFrame({u'Unsurvived':Survived_0,u'Survived':Survived_1})
df.plot(kind='bar', stacked=True)
plt.title(u"each Pclass survived")
plt.xlabel(u"Pcalss") 
plt.ylabel(u"number") 

fig = plt.figure()
fig.set(alpha=0.2)  # 设定图表颜色alpha参数

Survived_m = data_train.Survived[data_train.Sex == 'male'].value_counts()
Survived_f = data_train.Survived[data_train.Sex == 'female'].value_counts()
df=pd.DataFrame({u'MAN':Survived_m, u'FEMALE':Survived_f})
df.plot(kind='bar', stacked=True)
plt.title(u"the survived by sex")
plt.xlabel(u"Sex") 
plt.ylabel(u"number")


#然后我们再来看看各种舱级别情况下各性别的获救情况
fig=plt.figure()
fig.set(alpha=0.65) # 设置图像透明度，无所谓
plt.title(u"the survived by Sex&Pclass")

ax1=fig.add_subplot(141)
data_train.Survived[data_train.Sex == 'female'][data_train.Pclass != 3].value_counts().sort_index().plot(kind='bar', label="female highclass", color='#FA2479')
ax1.set_xticks([0,1])
ax1.set_xticklabels([u"Unsurvived", u"Survived"], rotation=0)
ax1.legend([u"FEMALE/RICH"], loc='best')

ax2=fig.add_subplot(142, sharey=ax1)
data_train.Survived[data_train.Sex == 'female'][data_train.Pclass == 3].value_counts().sort_index().plot(kind='bar', label='female, low class', color='pink')
ax2.set_xticklabels([u"Unsurvived", u"Survived"], rotation=0)
plt.legend([u"FEMALE/POOR"], loc='best')

ax3=fig.add_subplot(143, sharey=ax1)
data_train.Survived[data_train.Sex == 'male'][data_train.Pclass != 3].value_counts().sort_index().plot(kind='bar', label='male, high class',color='lightblue')
ax3.set_xticklabels([u"Unsurvived", u"Survived"], rotation=0)
plt.legend([u"MAN/RICH"], loc='best')

ax4=fig.add_subplot(144, sharey=ax1)
data_train.Survived[data_train.Sex == 'male'][data_train.Pclass == 3].value_counts().sort_index().plot(kind='bar', label='male low class', color='steelblue')
ax4.set_xticklabels([u"Unsurvived", u"Survived"], rotation=0)
plt.legend([u"MAN/POOR"], loc='best')
#RICH/POOR只是为了好写，不排除有体验生活的富人在3等舱

fig = plt.figure()
fig.set(alpha=0.2)  # 设定图表颜色alpha参数

Survived_0 = data_train.Embarked[data_train.Survived == 0].value_counts()
Survived_1 = data_train.Embarked[data_train.Survived == 1].value_counts()
df=pd.DataFrame({u'Unsurvived':Survived_0,u'Survived':Survived_1})
df.plot(kind='bar', stacked=True)
plt.title(u"the Survived by Embarked")
plt.xlabel(u"Embarked") 
plt.ylabel(u"number") 


gg = data_train.groupby(['SibSp','Survived'])
df = pd.DataFrame(gg.count()['PassengerId'])
print(df)

gp = data_train.groupby(['Parch','Survived'])
df = pd.DataFrame(gp.count()['PassengerId'])
print(df)

data_train.Cabin.value_counts()

fig = plt.figure()
fig.set(alpha=0.2)  # 设定图表颜色alpha参数

Survived_cabin = data_train.Survived[pd.notnull(data_train.Cabin)].value_counts()
Survived_nocabin = data_train.Survived[pd.isnull(data_train.Cabin)].value_counts()
df=pd.DataFrame({u'YES':Survived_cabin, u'NO':Survived_nocabin}).transpose()
df.plot(kind='bar', stacked=True)
plt.title(u"the Survived by IfHaveCabin")
plt.xlabel(u"CabinYES/NO") 
plt.ylabel(u"number")
plt.show()

