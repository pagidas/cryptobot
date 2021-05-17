import os
from rethinkdb import RethinkDB


rethink_host = os.environ['RETHINKDB_HOST']

r = RethinkDB()
r.connect(rethink_host, 28015).repl()

r.db_drop('test').run()
r.db_create('cryptobot').run()

r.db('cryptobot').table_create('coinbase_messages').run()
r.db('cryptobot').table_create('coinbase_product_subscriptions').run()
