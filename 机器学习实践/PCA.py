import matplotlib.pyplot as plt
import numpy as np

def mian():
    data = np.random.normal(10.0,5.0,1000)
    plt.hist(data,10)
    plt.legend()
    plt.show()
