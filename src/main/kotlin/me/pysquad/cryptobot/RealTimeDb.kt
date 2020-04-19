package me.pysquad.cryptobot

import com.rethinkdb.RethinkDB
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.config.DbConfig

object RealTimeDb {
    var connection: Connection? = null

    operator fun invoke(config: DbConfig) = apply {
        connection = r.connection().hostname(config.host).port(config.port).connect()
    }

    val rethinkWrapper: RethinkDB = r

    fun close() = connection?.close()
}
