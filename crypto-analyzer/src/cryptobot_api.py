from envyaml import EnvYAML
from utils.http import HttpClient
from utils.graphqli import GraphqlClient
from model.market import Product
from cryptonator import Cryptonator
from analysis.naivebayes import NaiveBayes


def parse_product_to_http(product):
    return "[\n  \"{product}\"\n]".format(product=product.title)


if __name__ == "__main__":

    # read configuration for host, port
    env = EnvYAML("./resources/env.yml")

    # create http_client
    http_client = HttpClient(env['gateway']['host'], env['gateway']['port'])

    # create graphql_client
    graphql_client = GraphqlClient(env['gateway']['host'], env['gateway']['port'])

    # check if there are current subscriptions
    # response = graphql_client.get_subs("/subscriptions", "POST")
    response = graphql_client.send_request(
        "/view",
        "POST",
        '{"query":"{\\n    subscriptions\\n}","variables":{}}'
    )
    subscriptions = response.json()['data'].get('subscriptions')
    # print(subscriptions)

    # create product to subscribe
    btc_eur_product = Product("BTC-USD")

    if btc_eur_product.title not in subscriptions:
        # subscibe
        body = parse_product_to_http(btc_eur_product)
        response = http_client.send_http_request("/subscribe", "POST", body)
        print(response.status_code)

    # start cryptonator
    cryptonator = Cryptonator(graphql_client)
    cryptonator.start(NaiveBayes)
