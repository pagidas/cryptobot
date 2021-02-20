import pandas as pd
import numpy as np
from abc import ABC, abstractmethod
from sklearn.linear_model import LinearRegression


class BasePredictor(ABC):
    def __init__(self, timeseries, horizon):
        self._data = pd.Series(np.array(timeseries))
        self.horizon = horizon
        self.trend_evaluator = LinearRegression()

    @abstractmethod
    def forecasting(self):
        raise NotImplementedError()

    @abstractmethod
    def should_buy(self):
        raise NotImplementedError()

    @abstractmethod
    def should_sell(self):
        raise NotImplementedError()

    @property
    def data(self):
        return self._data

    @data.setter
    def data(self, timeseries):
        self.data = pd.Series(np.array(timeseries))
