import requests


def subscribe_products(config_handler, *args):
    if not all(map(lambda product_id: isinstance(product_id, str), args)):
        raise ValueError

    subscribe_endpoint = config_handler.products_subscription_endpoint
    body = config_handler.post_request_body_template

    body['productIds'] = list(args)
    
    r = requests.post(subscribe_endpoint, json=body)
    print(r.text)
