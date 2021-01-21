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
