import sys
from rethinkdb import r


def connect_to_db(config_handler):
    r.connect(
        host=config_handler.rethink_host,
        port=config_handler.rethink_port,
        db=config_handler.rethink_db_name
    ).repl()


def print_feed():
    for change in r.table('messages').changes().run():
        print(change['new_val'])
        sys.stdout.flush()
