import numpy as np
from scipy.signal import argrelextrema


def _check_W_pattern(cand_prices, epsilon, p):
    lows = [cand_prices[1], cand_prices[3]]
    highs = [cand_prices[0], cand_prices[2], cand_prices[4]]

    if all(high > lows[0] for high in highs) and all(high > lows[1] for high in highs):
        print("Ws: ", cand_prices)
        # calculate distances for W
        # absolute distance between minimums in possible W pattern
        minD = np.abs(cand_prices[1] - cand_prices[3])
        # distance between middle maximum and second minimum in the possible W pattern
        D1 = cand_prices[2] - cand_prices[3]
        # absolute distance between middle and third maximum
        D2 = np.abs(cand_prices[2] - cand_prices[4])
        if D1 <= p * D2 and minD < epsilon:
            return 1
        else:
            return 0


def _check_M_pattern(cand_prices, epsilon, p):
    highs = [cand_prices[1], cand_prices[3]]
    lows = [cand_prices[0], cand_prices[2], cand_prices[4]]

    if all(high > lows[0] for high in highs) and all(high > lows[1] for high in highs):
        print("Ms: ", cand_prices)
        # calculate distances for M
        # absolute distance between maximums in possible M pattern
        maxD = np.abs(cand_prices[1] - cand_prices[3])
        # distance between second maximum and middle minimum in possible M pattern
        D1 = cand_prices[3] - cand_prices[2]
        # absolute distance between third minimum and middle minimum
        D2 = np.abs(cand_prices[2] - cand_prices[4])
        if D1 <= p * D2 and maxD < epsilon:
            return 1
        else:
            return 0


def get_MW_patterns(prices, epsilon=0.1, p=2):
    """
    :param prices: the input timeseries with the values of prices in time (can be either a python list or numpy array)
    :param epsilon: a parameter that defines the maximum accepted difference between the picks in the pattern
    :param p: a parameter that defines the distance between middle and edges in the pattern
    :return: two numpy arrays with the respective indexes for M and W patterns
    """
    # initialize the output arguments as empty lists
    cand_M = []
    cand_W = []
    # iterate through all prices and check for patterns in a price window of size five
    for index in range(len(prices[:-5])):
        # set as candidates the five consecutive indexes needed for a pattern (M or W)
        cand_idxs = np.array([index, index+1, index+2, index+3, index+4])
        # set the respective candidate prices
        cand_prices = np.array([prices[idx] for idx in cand_idxs])
        # check if there is a W or M pattern in current price window and add the indexes in the respective list
        if _check_W_pattern(cand_prices, epsilon, p):
            cand_W.append(cand_idxs)
        if _check_M_pattern(cand_prices, epsilon, p):
            cand_M.append(cand_idxs)

    return np.array(cand_M), np.array(cand_W)


def get_buy_price(timeseries, epsilon=0.1, p=2):
    """
    :param timeseries: input that contains the prices data in time (can be either a python list or numpy array)
    :param epsilon: a parameter that defines the maximum accepted difference between the picks in the pattern
    :param p: a parameter that defines the distance between middle and edges in the pattern
    :return: a numpy array with all the possible buy prices
    """
    # transform the input data into numpy array
    prices = np.array(timeseries)
    _, w = get_MW_patterns(prices, epsilon, p)
    if w.size != 0:
        return timeseries[w[:, 2]]
    else:
        return np.array([])

def get_sell_price(timeseries, epsilon=0.1, p=2):
    """
    :param timeseries: input that contains the prices data in time (can be either a python list or numpy array)
    :param epsilon: a parameter that defines the maximum accepted difference between the picks in the pattern
    :param p: a parameter that defines the distance between middle and edges in the pattern
    :return: a numpy array with all the possible sell prices
    """
    # transform the input data into numpy array
    prices = np.array(timeseries)
    m, _ = get_MW_patterns(prices, epsilon, p)
    if m.size != 0:
        return timeseries[m[:, 2]]
    else:
        return np.array([])
