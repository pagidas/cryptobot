from utils.adapter_request_helper import subscribe_products

def main():
    print("==================================")
    print("HELLO!! -- CRYPTO-ANALYZER SERVICE")
    print("==================================")

    subscribe_products(["BTC-EUR"])


if __name__ == "__main__":
    main()
