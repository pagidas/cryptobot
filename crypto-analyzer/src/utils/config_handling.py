import yaml

class ConfigHandler:
    config_data = None

    def __init__(self):
        try:
            with open("../resources/analayzer_configuration.yml", "r") as config_file:
                self.config_data = yaml.load(config_file)
        except OSError as err:
            print(err)