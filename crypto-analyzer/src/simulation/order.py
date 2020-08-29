class Order:
    def __init__(self, size, price):
        self.size = size
        self.price = price

    def close_order(self):
        raise NotImplementedError

    def is_covered(self, price):
        raise NotImplementedError


class BuyOrder(Order):
    def __init__(self, size, price):
        super().__init__(size, price)

    def __str__(self):
        return "Buy Order --> size = {}, price = {}".format(
            self.size,
            self.price
        )

    def close_order(self):
        # returns budget delta which is negative and coin delta which is positive
        return -self.price * self.size, self.size

    def is_covered(self, price):
        return self.price >= price


class SellOrder(Order):
    def __init__(self, size, price):
        super().__init__(size, price)

    def __str__(self):
        return "Sell Order --> size = {}, price = {}".format(
            self.size,
            self.price
        )

    def close_order(self):
        # returns budget delta which is positive and coin delta which is negative
        return self.price * self.size, -self.size

    def is_covered(self, price):
        return self.price <= price
