git import numpy as np
import pandas as pd
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LinearRegression


class NaiveBayes:
    """
        Predictor class is the class that tries to predict the trend in the data that is given as input.
    """

    def __init__(self, timeseries, horizon, preprocess=True, n_bins=10, n_lags=12):
        """
            :param timeseries: (python list or numpy array) input that contains the prices data in time
            :param horizon: integer that indicates the number of predicted values
            :param n_lags: (int) number of lag timeseries that will be used to fit the model
            :param n_bins: (int) number of bins that will be used to bin the data
            :param preprocess: (boolean) indicates whether there will be preprocess or not
        """
        self.data = pd.Series(np.array(timeseries))
        self.preprocess = preprocess
        self.horizon = horizon
        self.n_bins = n_bins
        self.n_lags = n_lags
        self.bin_means = {}
        self.model = GaussianNB()

        # setting two numpy arrays which will just be used as time indexes(1st column in the structure) for the
        # relative panda series objects
        self.train_index = np.arange(n_lags, len(self.data)).reshape(-1)
        self.test_index = np.arange(self.horizon).reshape(-1)
        # init panda series for remapped predictions within train data of the model
        self.remapped_insample_predictions = pd.Series(np.nan, index=self.train_index)
        # init empty panda series for the actual final forecasting of unknown future values of input timeseries
        self.forecasts = pd.Series(index = self.test_index)
        # init empty panda dataframe that will contain the lagged forecasted values
        self.forecast_frame = pd.DataFrame(np.nan, index=self.test_index, columns=range(self.n_lags))
        # set a linear regression model that will fit the forecasting values
        self.linear_reg = LinearRegression()

    def forecasting(self):
        """
        Main method that performs the whole forecasting procedure.
        """
        # preprocess timeseries by removing the trend and heteroscedasticity
        if self.preprocess:
            self.preprocessed_data = self.preprocess_data(self.data)

        # bin the data
        binned_series = self.binning_data(self.preprocessed_data)
        # map the category of each interval to the mean of the values in that interval
        for binn in range(1, self.n_bins + 1):
            self.bin_means[binn] = self.preprocessed_data[binned_series.to_numpy() == binn].mean()
        # create lagged timeseries frame from data
        lagged_frame = self.create_lagged_frame(binned_series, self.n_lags)

        # set train and target timeseries from lagged frame to fit the model
        train_x = lagged_frame.iloc[:, 1:]
        train_y = lagged_frame.iloc[:, 0]

        self.model.fit(train_x, train_y)
        insample_predictions = self.model.predict(train_x)

        for prediction in range(len(insample_predictions)):
            # print(insample_predictions[prediction])
            # remapped_insampled_prediction.append(bin_means[insample_predictions[prediction]])
            self.remapped_insample_predictions.iloc[prediction] = self.get_mean_from_class(
                                                                                    insample_predictions[prediction])

        # now out-of-sample forecasts need to be calculated iteratively since lagged values are required
        # setting the forecasting frame with the lagged timeseries of input data
        self.forecast_frame.iloc[0, 1:] = train_x.iloc[-1, :-1].values
        self.forecast_frame.iloc[0, 0] = train_y.iloc[-1]

        for i in range(self.horizon):
            pred = self.model.predict(self.forecast_frame.iloc[i, :].values.reshape(1, -1))
            pred_num = self.get_mean_from_class(pred[0])
            self.forecasts.iloc[i] = pred_num
            try:
                self.forecast_frame.iloc[i + 1, 1:] = self.forecast_frame.iloc[i, :-1].values
                self.forecast_frame.iloc[i + 1, 0] = pred[0]
            except IndexError:
                pass
        # retransform the forecasts for final prediction
        self.trend_test = np.arange(len(self.train_index), len(self.train_index) + len(self.test_index)).reshape(-1, 1)
        self.final_forecast = self.forecasts.cumsum() * ((self.trend_test + 1) ** (1 / 2)).reshape(-1) + self.data.iloc[-1]

    def get_mean_from_class(self, prediction):
        return self.bin_means[prediction]

    def preprocess_data(self, data):
        """
            :param data: (panda Series object) input that contains the prices data in time
            :return: (panda Series object) that contains the preprocessed data
        """
        # removing the trend in the data
        trend_removed = data.diff()
        # dealing with heteroscedasticity (variance of a variable is unequal across time) by dividing the data with √t
        t_timeseries = np.arange(len(data)).reshape(-1, 1)
        preprocessed_data = trend_removed / ((t_timeseries + 1) ** (1 / 2)).reshape(-1)
        # preprocessed_data = preprocessed_data[5:]
        return pd.Series(preprocessed_data)

    def binning_data(self, data):
        """
           :param data: (panda Series object) input that contains the prices data in time
           :return: (panda Series object) that contains the binned data
       """
        # bin the data
        bins = np.linspace(data.min(), data.max(), self.n_bins)
        binned = np.digitize(data, bins)

        binned_series = pd.Series(binned, index=data.index)

        return binned_series

    def create_lagged_frame(self, binned_series, n_lags):
        # To forecast future values we use the lagged value approach
        # We create lag versions of the data series in order to create a dataset to fit our model with
        lagged_list = []
        for s in range(n_lags+1):
            lagged_list.append(binned_series.shift(s))

        lagged_frame = pd.concat(lagged_list, 1).dropna()
        return lagged_frame

    def should_buy(self):
        """
            :return: (boolean) indicate if there should be put a buy order or not in the current timestep
        """
        # call the forecasting procedure to create forecast for future unseen data
        self.forecasting()
        # fit a linear regressor to the forecasting values
        self.linear_reg.fit(self.final_forecast.values.reshape(-1, 1), self.final_forecast.index)
        # check if the slope is positive or negative to decide buy order
        if np.sign(self.linear_reg.coef_):
            return True
        else:
            return False
