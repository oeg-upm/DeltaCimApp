import requests
import json
import time
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from os import listdir
from os.path import isfile, join
import rdflib
import sys

# experiemnt 1
post_exp = pd.read_csv("./POST_increasing.csv")
get_exp = pd.read_csv("./GET_increasing.csv")
get_exp["Size"] = get_exp['Size in Kb'] * 0.001
post_exp["Size"] = post_exp['Size(KB)'] * 0.001

get_exp["Time"] = get_exp['Average'] * 0.001
post_exp["Time"] = post_exp['Time(ms)'] * 0.001

plt.plot(get_exp['Size'], get_exp['Time'], '-+', label="GET requests")
plt.plot(post_exp['Size'], post_exp['Time'],  '-*', label="POST requests")

#plt.yscale('log')


plt.xlabel('Payload size (Mb)')
plt.ylabel('Response time (s)')
plt.ylim([0,35])
plt.xlim([0,10])
#plt.setp(ylabel='Response time (s)')
plt.legend(loc='upper left')
#plt.show()
plt.savefig("ex2-results.svg")