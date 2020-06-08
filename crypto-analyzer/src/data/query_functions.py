from rethinkdb import r

def get_prices(values_length):
    query = r.db('cryptobot')\
            .table('messages').pluck(['price', 'time', 'trade_id']).order_by(r.desc('time')).limit(values_length).run()
    prices = []
    trade_ids = []
    time = []
    # print(query[0])
    for d in query:
        prices.append(float(d['price']))
        trade_ids.append(int(d['trade_id']))
        time.append(d['time'].strftime('%M:%S'))

    return prices[::-1], trade_ids[::-1], time[::-1]

