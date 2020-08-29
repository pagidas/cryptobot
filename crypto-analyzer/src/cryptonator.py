import time
from analysis.pattern_detection import get_buy_price, get_sell_price
from simulation.base import Sim
from data.query_functions import get_latest_prices


def start():
    broker = Sim()

    while True:
        time.sleep(1)

        last_prices, ids, timestamps = get_latest_prices(50)

        # print(last_prices)

        broker.update_orders(last_prices[0])

        buy_prices = get_buy_price(last_prices)
        sell_prices = get_sell_price(last_prices)

        for bprice, sprice in zip(buy_prices, sell_prices):
            broker.open_order(0.01, bprice, 'buy')
            broker.open_order(0.01, sprice, 'sell')

        print("{}, {}, {}".format(broker.budget, broker.coins, broker.open_orders))


if __name__ == '__main__':
    from utils.config_handling import ConfigHandler
    from utils.db_connection import connect_to_db
    connect_to_db(ConfigHandler())
    start()
