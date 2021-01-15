from envyaml import EnvYAML
from utils.http import HttpClient


if __name__ == "__main__":

    # read configuration for host, port
    env = EnvYAML("./resources/env.yml")

    # create http_client
    http_client = HttpClient(env['gateway_host'], 80)

    # create graphql_client

    # start cryptonator
