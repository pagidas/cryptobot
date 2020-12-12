import sys
import time
from utils.config_handling import ConfigHandler
from utils.graphqli import GraphqlClient
from utils.subscriberi import subscribe_to
from cryptonator import Cryptonator


def main():
    time.sleep(1)
    print("Reading config file...")
    handler = ConfigHandler()
    gql_client = GraphqlClient(handler)
    cryptonator = Cryptonator(gql_client)

    active_subs = gql_client.get_subs()
    # cb_auth_request(handler)
    #
    # print("Subscribing...")
    # active_subs = list(get_active_subs(handler))
    # print(type(active_subs), active_subs)
    if not active_subs:
        print('Subscribing...')
        subscribe_to(handler, 'BTC-EUR')
    else:
        print("Already subscribed")

    sys.stdout.flush()

    cryptonator.start()


if __name__ == "__main__":
    main()
