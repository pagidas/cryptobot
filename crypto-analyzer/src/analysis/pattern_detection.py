import numpy as np
from scipy.signal import argrelextrema


def get_W_pattern_indexes(timeseries, maxs, mins, idx, epsilon, p):
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


def get_M_pattern_indexes(timeseries, maxs, mins, idx, epsilon, p):
    # calculate distances for M
    maxD = np.abs(timeseries[maxs[idx]] - timeseries[maxs[idx + 1]])
    # distance between second maximum and middle minimum
    D1 = timeseries[maxs[idx + 1]] - timeseries[mins[idx + 1]]
    # absolute distance between third minimum and middle minimum
    D2 = np.abs(timeseries[mins[idx + 1]] - timeseries[mins[idx + 2]])
    if D1 <= p * D2 and maxD < epsilon:
        return np.sort([maxs[idx], mins[idx], maxs[idx + 1],
                       mins[idx + 1], mins[idx + 2]]).tolist()


def get_MW_patterns(timeseries, epsilon=0.1, p=2):
    maximums = argrelextrema(timeseries, np.greater)[0]
    minimums = argrelextrema(timeseries, np.less)[0]
    # print("Local Maxs:", maximums)
    # print("Local Mins:", minimums)

    cand_M = []
    cand_W = []
    for index, (maxi, mini) in enumerate(zip(maximums, minimums)):
        if index < len(maximums) - 2:
            W_idxs = get_W_pattern_indexes(timeseries,maximums,minimums,index,epsilon,p)
            if W_idxs:
                cand_W.append(W_idxs)
            M_idxs = get_M_pattern_indexes(timeseries,maximums,minimums,index,epsilon,p)
            if M_idxs:
                cand_M.append(M_idxs)

    return cand_M, cand_W


