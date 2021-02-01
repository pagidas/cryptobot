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

            response = self.gql_client.send_request(
                "/view",
                "POST",
                "{{\"query\":\"{{\\n  messages(mostRecent: {}) {{\\n    productId\\n    price\\n    time\\n  }}\\n}}\",\"variables\":{{}} }}".format(50)
            )
            # print(response.json())

            list_of_messages = response.json()['data'].get('messages')
            last_prices = self.parse_messages(list_of_messages)
            # print(last_prices)
            if last_prices is []:
                continue

            broker.update_orders(last_prices[0])

            buy_prices = get_buy_price(last_prices)
            sell_prices = get_sell_price(last_prices)

            for price in buy_prices:
                broker.open_order(0.01, price, 'buy')

            for price in sell_prices:
                broker.open_order(0.01, price, 'sell')

            print("{}, {}, {}".format(broker.budget, broker.coins, broker.open_orders))

    @staticmethod
    def parse_messages(lom):
        prices = list()
        for msg in lom:
            for key, value in msg.items():
                if key == 'price':
                    prices.append(float(value))

        return prices[::-1]
