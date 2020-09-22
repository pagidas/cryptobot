import os
from rethinkdb import RethinkDB


rethink_host = os.environ['RETHINK_HOST']

r = RethinkDB()
r.connect(rethink_host, 28015).repl()

r.db_drop('test').run()
r.db_create('cryptobot').run()

r.db('cryptobot').table_create('messages').run()
r.db('cryptobot').table_create('product_subscriptions').run()

r.db('cryptobot').table('product_subscriptions').insert({
    'sub_id': 'coinbase-pro-subscription',
    'product_ids': ''
}).run()
