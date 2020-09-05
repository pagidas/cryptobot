import time
import sys
from utils.adapter_request_helper import subscribe_products, cb_auth_request, get_active_subs
from utils.db_connection import connect_to_db, print_feed
from utils.config_handling import ConfigHandler
import cryptonator


def main():
    print("==================================")
    print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    print("==================================")

    print("Reading config file...")
    handler = ConfigHandler()

    cb_auth_request(handler)

    print("Subscribing...")
    active_subs = list(get_active_subs(handler))
    print(type(active_subs), active_subs)
    if not active_subs:
        subscribe_products(handler, "BTC-EUR")
    else:
        print("Already subscribed")

    sys.stdout.flush()

    connect_to_db(handler)
    cryptonator.start()


if __name__ == "__main__":
    main()
