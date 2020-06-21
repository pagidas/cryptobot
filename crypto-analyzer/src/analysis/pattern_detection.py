import numpy as np
from scipy.signal import argrelextrema


def _check_W_pattern(data, epsilon, p):
    # calculate distances for W
    # absolute distance between minimums
    minD = np.abs(timeseries[mins[idx]] - timeseries[mins[idx + 1]])
    # distance between second minimum and middle maximum
    D1 = timeseries[maxs[idx + 1]] - timeseries[mins[idx + 1]]
    # absolute distance between middle and third maximum
    D2 = np.abs(timeseries[maxs[idx + 1]] - timeseries[maxs[idx + 2]])
    if D1 <= p * D2 and minD < epsilon:
        return np.sort([maxs[idx], mins[idx], maxs[idx + 1],
                       mins[idx + 1], maxs[idx - 1]]).tolist()


def _check_M_pattern(timeseries, maxs, mins, idx, epsilon, p):
    # calculate distances for M
    maxD = np.abs(timeseries[maxs[idx]] - timeseries[maxs[idx + 1]])
    # distance between second maximum and middle minimum
    D1 = timeseries[maxs[idx + 1]] - timeseries[mins[idx + 1]]
    # absolute distance between third minimum and middle minimum
    D2 = np.abs(timeseries[mins[idx + 1]] - timeseries[mins[idx + 2]])
    if D1 <= p * D2 and maxD < epsilon:
        return np.sort([maxs[idx], mins[idx], maxs[idx + 1],
                       mins[idx + 1], mins[idx + 2]]).tolist()


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
    for index in range(len(prices)):
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
