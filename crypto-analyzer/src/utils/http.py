import requests


class HttpClient:
    def __init__(self, host, port):
        self.base = "http://{}:{}".format(host, port)

    def send_http_request(self, endpoint, method, body):
        headers = {
            'Content-Type': 'application/json'
        }
        return requests.request(method,
                                self.base + endpoint,
                                headers=headers,
                                data=body)
