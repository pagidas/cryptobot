import time
from analysis.pattern_detection import get_buy_price, get_sell_price
from simulation.base import Sim


class Cryptonator:
    """
        Cryptonator class is the main analyzer class in which all the math magic
        happens.
    """
    def __init__(self, client):
        self.gql_client = client

    def start(self):
        broker = Sim()

        while True:
            time.sleep(1)

            list_of_messages = self.gql_client.get_most_recent_messages(50)
            # print(list_of_messages)

            last_prices = self.parse_messages(list_of_messages)
            # print(last_prices)
            if last_prices is []:
                continue

            broker.update_orders(last_prices[0])

            buy_prices = get_buy_price(last_prices)
            sell_prices = get_sell_price(last_prices)

            for bprice, sprice in zip(buy_prices, sell_prices):
                broker.open_order(0.01, bprice, 'buy')
                broker.open_order(0.01, sprice, 'sell')

            print("{}, {}, {}".format(broker.budget, broker.coins, broker.open_orders))

    @staticmethod
    def parse_messages(lom):
        prices = list()
        for msg in lom:
            for key, value in msg.items():
                if key == 'price':
                    prices.append(float(value))

        return prices[::-1]
