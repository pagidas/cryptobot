import requests


def cb_auth_request(config_handler):
    body = {
        "credentials": {
            "user": config_handler.username,
            "password": config_handler.password
        }
    }

    auth_endpoint = config_handler.auth

    r = requests.post(auth_endpoint, json=body)

    # print(r.json()['authToken'])
    config_handler.auth_token = r.json()['authToken']


def subscribe_products(config_handler, *args):
    if not all(map(lambda product_id: isinstance(product_id, str), args)):
        raise ValueError

    headers = {"Authorization": "Basic {}".format(
        config_handler.auth_token)}
    subscribe_endpoint = config_handler.products_subscription_endpoint
    body = config_handler.post_request_body_template

    body['productIds'] = list(args)

    r = requests.post(subscribe_endpoint, json=body, headers=headers)
    print(r.text)


def get_active_subs(config_handler):
    headers = {"Authorization": "Basic {}".format(
        config_handler.auth_token)}
    subscriptions_endpoint = config_handler.subscriptions
    r = requests.get(subscriptions_endpoint, headers=headers)
    return r.json()
