import db.datasource as datasource


MESSAGES = "messages"


def get_coinbase_message_feed():
    connection = datasource.connect()
    return datasource.ctx.table(MESSAGES).changes().run(connection)
