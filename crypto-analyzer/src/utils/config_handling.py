import yaml
import os


class ConfigHandler:
    config_data = None

    def __init__(self):
        try:
            with open("./resources/analyzer_configuration.yml",
                      "r") as config_file:
                self.config_data = yaml.load(config_file, Loader=yaml.Loader)
        except OSError as err:
            print(err)

        if "RETHINK_DB_HOST" in os.environ:
            self.config_data['rethinkdb']['host'] =\
                os.environ['RETHINK_DB_HOST']

        if "CB_HOST" in os.environ:
            self.config_data['coinbase_adapter']['host'] =\
                os.environ['CB_HOST']

    def __construct_url(self, endpoint):
        if not self.config_data:
            raise ValueError

        return 'http://{host}:{port}{endpoint}'.format(
            host=self.config_data['coinbase_adapter']['host'],
            port=self.config_data['coinbase_adapter']['port'],
            endpoint=endpoint
        )

    @property
    def products_subscription_endpoint(self):
        return self.__construct_url(
            self.config_data['coinbase_adapter']['subscribe']
        )

    @property
    def auth(self):
        return self.__construct_url(
            self.config_data['coinbase_adapter']['auth']
        )

    @property
    def rethink_host(self):
        return self.config_data['rethinkdb']['host']

    @property
    def rethink_port(self):
        return self.config_data['rethinkdb']['port']

    @property
    def post_request_body_template(self):
        return self.config_data['coinbase_adapter']['subscribe_body']

    @property
    def rethink_db_name(self):
        return self.config_data['rethinkdb']['db_name']

    @property
    def username(self):
        return os.environ['COINBASE_ADAPTER_BASIC_AUTH_USER']

    @property
    def password(self):
        return os.environ['COINBASE_ADAPTER_BASIC_AUTH_PWD']

    @property
    def subscriptions(self):
        return self.__construct_url(
            self.config_data['coinbase_adapter']['subscriptions']
        )
