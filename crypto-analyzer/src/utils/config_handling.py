import yaml

class ConfigHandler:
    config_data = None

    def __init__(self):
        try:
            with open("./resources/analyzer_configuration.yml", "r") as config_file:
                self.config_data = yaml.load(config_file, Loader=yaml.Loader)
        except OSError as err:
            print(err)

    @property
    def products_subscription_endpoint(self):
        if not self.config_data:
            raise ValueError

        return 'http://{host}:{port}{endpoint}'.format(
            host = self.config_data['coinbase_adapter']['host'],
            port = self.config_data['coinbase_adapter']['port'],
            endpoint = self.config_data['coinbase_adapter']['subscribe_products_endpoint']
        )

    @property
    def rethink_host(self):
        return self.config_data['rethinkdb']['host']

    @property
    def rethink_port(self):
        return self.config_data['rethinkdb']['port']

    @property
    def post_request_body_template(self):
        return self.config_data['coinbase_adapter']['post_request_body_template']

    @property
    def rethink_db_name(self):
        return self.config_data['rethinkdb']['db_name']