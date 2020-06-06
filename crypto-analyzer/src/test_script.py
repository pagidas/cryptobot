import time
from utils.adapter_request_helper import subscribe_products
from utils.db_connection import connect_to_db, print_feed
from utils.config_handling import ConfigHandler
from utils.anal_utils import get_latest_prices
import matplotlib.pyplot as plt
import numpy as np

if __name__ == "__main__":
    print("==================================")
    print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    print("==================================")

    print("Reading config file...")
    handler = ConfigHandler()

    # print("Sleeping for ten seconds...")

    # subscribe_products(handl er, "BTC-EUR")

    connect_to_db(handler)
    # time.sleep(10)
    # print_feed()
    # values = get_latest_prices(20)
    # print(values)

    budget_euro = 1000000
    budget_coin = 0
    prices  = []
    q_size = 2000
    while len(prices) < q_size:
        prices, trade_ids, time = get_latest_prices(q_size)

    max_idx = np.argmax(prices)
    min_idx = np.argmin(prices)

    percentage = 0.01
    gap = prices[max_idx]-prices[min_idx]
    max_thres = prices[max_idx] - gap*percentage
    min_thres = prices[min_idx] + gap*percentage

    # tick_y = prices[max_idx]
    # plt.plot(prices)
    # plt.plot([min_idx, max_idx], [tick_y, tick_y], '|')
    # plt.show()