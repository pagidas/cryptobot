from utils.adapter_request_helper import subscribe_products
from utils.db_connection import connect_to_db, print_feed

def main():
    print("==================================")
    print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    print("==================================")

    subscribe_products(["BTC-EUR"])

    connect_to_db()
    print_feed()


if __name__ == "__main__":
    main()
