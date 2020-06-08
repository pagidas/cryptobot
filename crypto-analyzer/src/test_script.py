import time
from utils.adapter_request_helper import subscribe_products
from utils.db_connection import connect_to_db, print_feed
from utils.config_handling import ConfigHandler
import matplotlib.pyplot as plt
from analysis.pattern_detection import get_mw
import numpy as np
# from scipy.signal import argrelextrema
#
#
# def get_candidate_W_indexes(timeseries, maxs, mins, idx, epsilon, p):
#     # calculate distances for W
#     # absolute distance between minimums
#     minD = np.abs(timeseries[mins[idx]] - timeseries[mins[idx + 1]])
#     # distance between second minimum and middle maximum
#     D1 = timeseries[maxs[idx + 1]] - timeseries[mins[idx + 1]]
#     # absolute distance between middle and third maximum
#     D2 = np.abs(timeseries[maxs[idx + 1]] - timeseries[maxs[idx + 2]])
#     if D1 <= p * D2 and minD < epsilon:
#         return np.sort([maxs[idx], mins[idx], maxs[idx + 1],
#                        mins[idx + 1], maxs[idx - 1]]).tolist()
#
#
# def get_candidate_M_indexes(timeseries, maxs, mins, idx, epsilon, p):
#     # calculate distances for M
#     maxD = np.abs(timeseries[maxs[idx]] - timeseries[maxs[idx + 1]])
#     # distance between second maximum and middle minimum
#     D1 = timeseries[maxs[idx + 1]] - timeseries[mins[idx + 1]]
#     # absolute distance between third minimum and middle minimum
#     D2 = np.abs(timeseries[mins[idx + 1]] - timeseries[mins[idx + 2]])
#     if D1 <= p * D2 and maxD < epsilon:
#         return np.sort([maxs[idx], mins[idx], maxs[idx + 1],
#                        mins[idx + 1], mins[idx + 2]]).tolist()
#
#
# def get_mw(timeseries, epsilon=0.1, p=2):
#     maximums = argrelextrema(timeseries, np.greater)[0]
#     minimums = argrelextrema(timeseries, np.less)[0]
#     print("Local Maxs:", maximums)
#     print("Local Mins:", minimums)
#
#     cand_M = []
#     cand_W = []
#     for index, (maxi, mini) in enumerate(zip(maximums, minimums)):
#         if index < len(maximums) - 2:
#             W_idxs = get_candidate_W_indexes(timeseries,maximums,minimums,index,epsilon,p)
#             if W_idxs:
#                 cand_W.append(W_idxs)
#             M_idxs = get_candidate_M_indexes(timeseries,maximums,minimums,index,epsilon,p)
#             if M_idxs:
#                 cand_M.append(M_idxs)
#
#     return cand_M, cand_W


if __name__ == "__main__":
    # print("==================================")
    # print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    # print("==================================")
    #
    # print("Reading config file...")
    # handler = ConfigHandler()
    #
    # # print("Sleeping for ten seconds...")
    #
    # # subscribe_products(handl er, "BTC-EUR")
    #
    # connect_to_db(handler)
    # # time.sleep(10)
    # # print_feed()
    # # values = get_latest_prices(20)
    # # print(values)
    #
    # budget_euro = 1000000
    # budget_coin = 0
    # prices  = []
    # q_size = 2000
    # while len(prices) < q_size:
    #     prices, trade_ids, time = get_latest_prices(q_size)
    #
    # max_idx = np.argmax(prices)
    # min_idx = np.argmin(prices)
    #
    # percentage = 0.01
    # gap = prices[max_idx]-prices[min_idx]
    # max_thres = prices[max_idx] - gap*percentage
    # min_thres = prices[min_idx] + gap*percentage

    # tick_y = prices[max_idx]
    # plt.plot(prices)
    # plt.plot([min_idx, max_idx], [tick_y, tick_y], '|')
    # plt.show()

    x = [0.2, 0, -1, 0, 2, 1, 0.5, 1, 2, 0, 0.1, 0.2, 0, 2, 0.5, 1, 0.5, 2, 1.5, 1, 0.5, 0, 3, 2, 3, 0, 1, 0.5, 0.1]
    Ms, Ws = get_mw(np.array(x))