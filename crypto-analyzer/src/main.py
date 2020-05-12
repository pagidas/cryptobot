import time
from utils.adapter_request_helper import subscribe_products
from utils.db_connection import connect_to_db, print_feed
from utils.config_handling import ConfigHandler


def main():
    print("==================================")
    print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    print("==================================")

    print("Reading config file...")
    handler = ConfigHandler()

    print("Sleeping for ten seconds...")
    time.sleep(10)
    subscribe_products(handler, "BTC-EUR")

    connect_to_db(handler)
    print_feed()


if __name__ == "__main__":
    main()
