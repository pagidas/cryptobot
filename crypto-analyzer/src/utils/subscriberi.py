import requests


def subscribe_to(handler, product):
    if type(product) is not str:
        raise TypeError

    url = handler.products_subscription_endpoint
    payload = "[\n  \"{product}\"\n]".format(product=product)
    headers = {
      'Content-Type': 'application/json'
    }

    response = requests.request("POST", url, headers=headers, data = payload)

    print(response.text.encode('utf8'))
