import requests
HOST = "http://coinbase_adapter:9000"
subscribe_endpoint = HOST + "/api/market/subscribe"

def subscribe_product(product):
    body = {"type": "Subscribe",
            "productIds": [
                product 
            ],
            "channels": [
                "ticker"
            ]}

    r = requests.post(subscribe_endpoint, json=body)
    print(r.text)

def subscribe_products(product_ids):
    body = {"type": "Subscribe",
            "productIds": product_ids,
            "channels": [
                "ticker"
            ]}

    r = requests.post(subscribe_endpoint, json=body)
    print(r.text)