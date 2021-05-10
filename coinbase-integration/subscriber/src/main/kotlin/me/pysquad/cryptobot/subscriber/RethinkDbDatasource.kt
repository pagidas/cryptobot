package me.pysquad.cryptobot.subscriber

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection

// db name
const val CRYPTOBOT = "cryptobot"

// table names
const val COINBASE_MESSAGES = "coinbase_messages"
const val COINBASE_PRODUCT_SUBSCRIPTIONS = "coinbase_product_subscriptions"

class RethinkDbDatasource(private val rethinkDbConfig: RethinkDbConfiguration) {

    val ctx: RethinkDB = RethinkDB.r
    val connection: Connection

    init { connection = connect(ctx) }

    private fun connect(r: RethinkDB): Connection =
        r.connection()
            .hostname(rethinkDbConfig.host)
            .port(rethinkDbConfig.port)
            .db(CRYPTOBOT)
            .connect()
}