import requests


class GraphqlClient:
    """
        This is a client class. It communicates with the graphql server and
        requests various data fetches from rethinkdb.
    """
    def __init__(self, handler):
        self.handler = handler

    def get_subs(self):
        """
        Speaks with graphql service to receive all current subscriptions
        :return: a list of strings representing the active subs.
        For example, ['BTC-EUR']
        """
        url = self.handler.graphql_subscriptions_endpoint
        print("Getting subscriptions from {}".format(url))
        payload = '{"query":"{\\n    subscriptions\\n}","variables":{}}'
        headers = {
            'Content-Type': 'application/json'
        }
        response = requests.request("POST", url, headers=headers, data=payload)

        return response.json()['data']['subscriptions']

    def get_most_recent_messages(self, number):
        """
        In this function we request from graphql service to fetch the most
        recent messages from rethinkdb
        :param number: is the number of most recent messages
        :return: a list of dictionaries
        """
        url = self.handler.graphql_most_recent_endpoint
        payload = "{{\"query\":\"{{\\n  messages(mostRecent: {}) {{\\n    productId\\n    price\\n    time\\n  }}\\n}}\",\"variables\":{{}} }}".format(number)
        headers = {
            'Content-Type': 'application/json'
        }

        response = requests.request("POST", url, headers=headers, data=payload)

        return response.json()['data']['messages']

