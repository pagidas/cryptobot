from simulation.order import SellOrder, BuyOrder


class Sim:

    def __init__(self, fiat=200000, coins=5):
        self._open_orders = list()
        self.budget = fiat
        self.coins = coins

    @property
    def open_orders(self):
        return self._open_orders

    @property
    def state(self):
        return {
            "budget": self.budget,
            "coins": self.coins,
            "open_orders": [str(order) for order in self._open_orders]
        }

    def open_order(self, size, price, side):
        my_order = None

        if str(side).lower().strip() == "sell" and self.coins >= size:
            my_order = SellOrder(size, price)
        elif str(side).lower().strip() == "buy" and self.budget >= size * price:
            my_order = BuyOrder(size, price)

        if my_order:
            self._open_orders.append(my_order)

    def update_orders(self, price):
        closing_orders = []
        for order in self._open_orders:
            if order.is_covered(price):
                closing_orders.append(order)
                budget_delta, coin_delta = order.close_order()
                self.budget += budget_delta
                self.coins += coin_delta

        self._open_orders = [order for order in self._open_orders if order not in closing_orders]
