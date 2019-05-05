import numpy as np 
import pandas as pd 
import matplotlib 
import missingno as msno 
import geoplot as gplt
import matplotlib.pyplot as plt

train_df = pd.read_csv("/home/jason/Documents/ML/titanic/train.csv")
msno.matrix(train_df, labels=True)
msno.bar(train_df)
msno.dendrogram(train_df)
msno.heatmap(train_df)

plt.show()
