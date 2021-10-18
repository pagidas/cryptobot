from rethinkdb import r


CRYPTOBOT = "cryptobot"
ctx = r


def connect(config):
    return ctx.connect(db=CRYPTOBOT, host=config["rethinkdb"]["host"])
