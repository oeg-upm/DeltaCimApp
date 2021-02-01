import statsmodels.api as sm
import requests
import json
import time
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from os import listdir
from os.path import isfile, join
import rdflib
from scipy.stats import pearsonr

datasets = ["./GET_Experiment3.csv", "POST_Experiment1.csv", "POST_Experiment2.csv"]
statistics = {}
for dataset in datasets:
	data_raw = pd.read_csv(dataset)
	data_filtered = data_raw[data_raw["ExecutionNumber"] == "Total5Execution"]
	if "POST_Experiment2.csv" in dataset:
		data_filtered["Average"] = data_filtered["Average"] * 0.001

	pearson_value = pearsonr(data_filtered["Samples"],data_filtered["Average"])
	x = sm.add_constant(data_filtered[["Samples"]]) 
	ols = sm.OLS(data_filtered[["Average"]], x)
	results = ols.fit()

	statistics[dataset] = {'value' : pearson_value[0], 'p-value' : pearson_value[1], 'r2' : results.rsquared, 'r2-adj': results.rsquared_adj}
	
	stasmodel_prediction_ci = results.get_prediction(x).summary_frame(alpha=0.01)

	plt.plot(data_filtered['Samples'], data_filtered['Average'], label='Average',color="orange")

	plt.plot(data_filtered['Samples'], stasmodel_prediction_ci["mean"], label='Average',color="red")
	plt.plot(data_filtered['Samples'], stasmodel_prediction_ci["mean_ci_lower"], color="red", linestyle=":")
	plt.plot(data_filtered['Samples'], stasmodel_prediction_ci["mean_ci_upper"], color="red", linestyle=":")
	plt.savefig(dataset+'.png')
print(statistics)