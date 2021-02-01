import requests
import json
import time
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from os import listdir
from os.path import isfile, join
import rdflib

# experiemnt 1
get_exp1 = pd.read_csv("./GET_Experiment3.csv")
get_exp_base = pd.read_csv("./GET_Baseline.csv")

post_exp1 = pd.read_csv("./POST_Experiment1.csv")
post_exp2 = pd.read_csv("./POST_Experiment2.csv")
post_exp_base = pd.read_csv("./POST_Baseline.csv")


get_total = get_exp1[get_exp1["Iteration"] == "IterationNumber"]
get_total_base = get_exp_base[get_exp_base["Iteration"] == "IterationNumber"]

post_total_base = post_exp_base[post_exp_base["Iteration"] == "IterationNumber"]
post1_total = post_exp1[post_exp1["Iteration"] == "IterationNumber"]
post2_total = post_exp2[post_exp2["Iteration"] == "IterationNumber"]
#post2_total["Average"] = post2_total["Average"] * 0.001


fig, axs = plt.subplots(3)

axs[0].plot(get_total['Samples'], get_total['Average'], label='Average',color="orange")
axs[0].plot(get_total_base['Samples'], get_total_base['Average'], label='Average',color="red")

axs[1].plot(post1_total['Samples'], post1_total['Average'], label='Average',color="green")
axs[1].plot(post_total_base['Samples'], post_total_base['Average'], label='Average',color="green")


axs[2].plot(post2_total['Samples'], post2_total['Average'], label='Average',color="blue")
axs[2].plot(post_total_base['Samples'], post_total_base['Average'], label='Average',color="blue")

#axs[2].set_ylim([0,800])

plt.setp(axs, xlabel='Number of parallel requests')
plt.setp(axs[0:2], ylabel='Response time (ms)')
plt.setp(axs[2], ylabel='Response time (s)')
plt.show()