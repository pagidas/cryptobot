from rethinkdb import r

def connect_to_db():
    r.connect(host='rethinkdb', db='cryptobot').repl()

def print_feed():
    for change in r.table('messages').changes().run():
        print(change['new_val'])