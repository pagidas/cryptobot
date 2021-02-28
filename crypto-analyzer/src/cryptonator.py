import time
import pandas as pd
# from analysis.pattern_detection import get_buy_price, get_sell_price
from simulation.base import Sim
from analysis.base_predictor import BasePredictor


class Cryptonator:
    """
        Cryptonator class is the main analyzer class in which all the math magic
        happens.
    """
    def __init__(self, client):
        self.gql_client = client
        self._predictor = None

    def get_dataframe(self, samples=10):
        response = self.gql_client.send_request(
            "/view",
            "POST",
            f"{{\"query\":\"{{\\n  messages(mostRecent: {samples}) {{\\n    productId\\n    price\\n    time\\n  }}\\n}}\", \
            \"variables\":{{}} }}"
        )
        list_of_messages = response.json()['data'].get('messages')
        return pd.DataFrame(list_of_messages)

    def start(self, predictor_cls):
        broker = Sim(coins=0)
        self._init_predictor(predictor_cls, 20)  # Horizon value is half the length of the timeseries

        prev_price = None
        while True:
            time.sleep(1)

            response = self.gql_client.send_request(
                "/view",
                "POST",
                "{{\"query\":\"{{\\n  messages(mostRecent: {}) {{\\n    productId\\n    price\\n    time\\n  }}\\n}}\","
                "\"variables\":{{}} }}".format(50)
            )
            # print(response.json())

            list_of_messages = response.json()['data'].get('messages')
            last_prices = self._parse_messages(list_of_messages)
            # print(last_prices)
            if not last_prices:
                continue

            broker.update_orders(last_prices[-1])

            # buy_prices = get_buy_price(last_prices)
            # sell_prices = get_sell_price(last_prices)

            # for price in buy_prices:
            #     broker.open_order(0.01, price, 'buy')

            # for price in sell_prices:
            #     broker.open_order(0.01, price, 'sell')

            # TODO constraint buy orders with a better way because they are too frequent
            if self._predictor.should_buy() and last_prices[-1] != prev_price:
                broker.open_order(0.5, last_prices[-1], 'buy')
                broker.open_order(0.5, last_prices[-1] * 1.001, 'sell')

            prev_price = last_prices[-1]
            print(f"\nprice: {last_prices[-1]}€, budget: {broker.budget}€, Coins: {broker.coins}")
            print(broker.performance)

    @staticmethod
    def _parse_messages(lom):
        prices = list()
        for msg in lom:
            for key, value in msg.items():
                if key == 'price':
                    prices.append(float(value))

        return prices[::-1]

    def _init_predictor(self, pred_cls, horizon=25):
        if not issubclass(pred_cls, BasePredictor):
            raise ValueError()

        last_prices = []

        while not last_prices:
            response = self.gql_client.send_request(
                "/view",
                "POST",
                "{{\"query\":\"{{\\n  messages(mostRecent: {}) {{\\n    productId\\n    price\\n    time\\n  }}\\n}}\","
                "\"variables\":{{}} }}".format(50)
            )
            # print(response.json())

            list_of_messages = response.json()['data'].get('messages')
            last_prices = self._parse_messages(list_of_messages)
            # print(last_prices)

        self._predictor = pred_cls(last_prices, horizon)
