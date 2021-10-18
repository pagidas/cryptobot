import db.datasource as datasource


MESSAGES = "coinbase_messages"


def get_coinbase_message_feed(config):
    connection = datasource.connect(config)
    return datasource.ctx.table(MESSAGES).changes().run(connection)
