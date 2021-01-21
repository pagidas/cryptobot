import requests


class GraphqlClient:
    """
        This is a client class. It communicates with the proxy server and
        requests various data fetches from rethinkdb.
    """
    def __init__(self, host, port):
        self.base = "http://{}:{}".format(host, port)

    def send_request(self, endpoint, method, payload):
        url = self.base + endpoint
        headers = {
            'Content-Type': 'application/json'
        }
        return requests.request(method, url, headers=headers, data=payload)

    def get_subs(self, endpoint, method):
        """
        Speaks with graphql service to receive all current subscriptions
        :param endpoint: the endpoint for getting current subscriptions
        :param method: the method of the request
        :return: http response.
        """
        url = self.base + endpoint
        payload = '{"query":"{\\n    subscriptions\\n}","variables":{}}'
        headers = {
            'Content-Type': 'application/json'
        }
        return requests.request(method, url, headers=headers, data=payload)

    def get_most_recent_messages(self, endpoint, method, number=100):
        """
        In this function we request from graphql service to fetch the most
        recent messages from rethinkdb
        :param endpoint: the endpoint for getting recent messages from graphql server
        :param method: the method of the request
        :param number: is the number of most recent messages (default is 100)
        :return: http response.
        """
        url = self.base + endpoint
        payload = "{{\"query\":\"{{\\n  messages(mostRecent: {}) {{\\n    productId\\n    price\\n    time\\n  " \
                  "}}\\n}}\",\"variables\":{{}} }}".format(number)
        headers = {
            'Content-Type': 'application/json'
        }

        return requests.request(method, url, headers=headers, data=payload)
