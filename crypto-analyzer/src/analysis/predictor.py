import numpy as np
import pandas as pd
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LinearRegression


def preprocess_data(timeseries):
    """
    :param timeseries: input that contains the prices data in time (can be either a python list or numpy array)
    :return: numpy array that contains the preprocessed timeseries
    """
    # removing the trend in the data
    trend_removed = np.gradient(timeseries)
    # dealing with heteroscedasticity (variance of a variable is unequal across time) by dividing the data with √t
    t_timeseries = np.arange(len(timeseries)).reshape(-1, 1)
    preprocessed_data = trend_removed / ((t_timeseries + 1) ** (1 / 2)).reshape(-1)

    return preprocessed_data


def binning_data(data, n_bins):
    # bin the data
    bins = np.linspace(data.min(), data.max(), n_bins)
    binned = np.digitize(data, bins)

    binned_series = pd.Series(binned, index=data.index)

    return binned_series


def create_lagged_frame(binned_series, n_lags):
    # To forecast future values we use the lagged value approach
    # We create lag versions of the data series in order to create a dataset to fit our model with
    lagged_list = []
    for s in range(n_lags+1):
        lagged_list.append(binned_series.shift(s))

    lagged_frame = pd.concat(lagged_list, 1).dropna()

def should_buy(timeseries, horizon, preprocess=True, n_bins=10, n_lags=12):
    """
    :param timeseries: input that contains the prices data in time (can be either a python list or numpy array)
    :param horizon: integer that indicates the number of predicted values
    :param n_lags:
    :param n_bins:
    :param preprocess:
    :return: boolean to indicate a buy order
    """
    timeseries = np.array(timeseries)
    # preprocess timeseries by removing the trend and heteroscedasticity
    if preprocess:
        preprocessed_data = preprocess_data(timeseries)
    else:
        preprocessed_data = timeseries

    # bin the data
    binned_series = binning_data(preprocessed_data, n_bins)
    # map the category of each interval to the mean of the values in that interval
    bin_means = {}
    for binn in range(1, n_bins + 1):
        bin_means[binn] = preprocessed_data[binned_series.to_numpy() == binn].mean()

    # create lagged timeseries frame from data
    lagged_frame = create_lagged_frame(binned_series, n_lags)

    # set train and target timeseries from lagged frame to fit the model
    train_X = lagged_frame.iloc[:, 1:]
    train_y = lagged_frame.iloc[:, 0]

    model = GaussianNB()
    model.fit(train_X, train_y)
    insample_predictions = model.predict(train_X)
    remapped_insampled_prediction = []
    for prediction in range(len(insample_predictions)):
        remapped_insampled_prediction.append(bin_means[insample_predictions[prediction]])

