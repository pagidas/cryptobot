from rethinkdb import r


CRYPTOBOT = "cryptobot"
ctx = r


def connect():
    # TODO pass config object
    return ctx.connect(db=CRYPTOBOT)
