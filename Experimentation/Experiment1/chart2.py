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

show = sys.argv[1]
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

if "GET" in show:
	plt.plot(get_total['Samples'], get_total['Average'], label="GET (xml to json-ld)", color="red")
	plt.plot(get_total_base['Samples'], get_total_base['Average'], '--', label="GET baseline")
	
if "POST1" in show:
	plt.plot(post1_total['Samples'], post1_total['Average'], label="POST (json-ld to xml)")
	plt.plot(post_total_base['Samples'], post_total_base['Average'], '--',label="POST baseline")

if "POST2" in show:
	plt.plot(post2_total['Samples'], post2_total['Average'], label='POST (xml to json-ld)')
	plt.plot(post_total_base['Samples'], post_total_base['Average'], '--', label="POST baseline")
plt.yscale('symlog')
#axs[1].plot(post1_total['Samples'], post1_total['Average'], label='Average',color="green")
#axs[1].plot(post_total_base['Samples'], post_total_base['Average'], label='Average',color="green")


#axs[2].plot(post2_total['Samples'], post2_total['Average'], label='Average',color="blue")
#axs[2].plot(post_total_base['Samples'], post_total_base['Average'], label='Average',color="blue")


plt.xlabel('Number of parallel requests')
plt.ylabel('Response time (ms)')
#plt.setp(ylabel='Response time (s)')
plt.legend(loc='best')
plt.show()