import os
from rethinkdb import RethinkDB


rethink_host = os.environ['RETHINKDB_HOST']
rethink_port = 28015

r = RethinkDB()
r.connect(rethink_host, rethink_port).repl()

r.db_drop('test').run()
r.db_create('cryptobot').run()

r.db('cryptobot').table_create('coinbase_messages').run()
r.db('cryptobot').table_create('coinbase_product_subscriptions').run()
