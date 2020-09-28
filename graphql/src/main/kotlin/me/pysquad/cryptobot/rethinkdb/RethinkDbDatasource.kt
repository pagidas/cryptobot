package me.pysquad.cryptobot.rethinkdb

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import javax.inject.Singleton

// db name
const val CRYPTOBOT = "cryptobot"

// table names
const val MESSAGES = "messages"
const val PRODUCT_SUBSCRIPTIONS = "product_subscriptions"

@Singleton
class RethinkDbDatasource(private var rethinkDbConfig: RethinkDbConfiguration) {

    var ctx: RethinkDB = RethinkDB.r
    lateinit var connection: Connection

    init { connect(ctx) }

    private fun connect(r: RethinkDB) = apply {
        connection = r.connection()
                .hostname(rethinkDbConfig.host)
                .port(rethinkDbConfig.port.toInt())
                .db(CRYPTOBOT)
                .connect()
    }
}