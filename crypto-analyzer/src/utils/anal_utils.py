import sys
from rethinkdb import r
import numpy as np

def get_latest_prices(values_length):
    return r.db('cryptobot')\
            .table('messages').pluck(['price','time']).order_by(r.desc('time')).limit(values_length).run()
