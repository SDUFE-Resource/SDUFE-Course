import matplotlib.pyplot as plt  
import pandas as pd
import numpy as np
from numpy.random import randint
x=['Product A','Product B','Product C','Product D','Product E']
y=[randint(1000,1100),randint(1000,1100),randint(1000,1100),randint(1000,1100),np.random.randint(1000,1100)]
plt.bar(x,y)
plt.show()
