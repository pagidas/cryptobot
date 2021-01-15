from envyaml import EnvYAML
from utils.http import HttpClient
from utils.graphqli import GraphqlClient
from model.market import Product


def parse_product_to_http(product):
    return "[\n  \"{product}\"\n]".format(product=product.title)


if __name__ == "__main__":

    # read configuration for host, port
    env = EnvYAML("./resources/env.yml")

    # create http_client
    http_client = HttpClient(env['gateway_host'], 80)

    # create graphql_client
    graphql_client = GraphqlClient(env['gateway_host'], 80)

    # check if there are current subscriptions
    # response = graphql_client.get_subs("/subscriptions", "POST")

    #subscibe
    body = parse_product_to_http(Product("BTC-EUR"))
    http_client.send_http_request("/subscribe", "POST", body)

    # start cryptonator
