package me.pysquad.cryptobot.subscriber.rethinkdb

import me.pysquad.cryptobot.subscriber.configReader

data class RethinkDbConfiguration(
        val port: Int = configReader.getInt("rethinkdb.port"),
        val host: String = configReader.getString("rethinkdb.host")
)

