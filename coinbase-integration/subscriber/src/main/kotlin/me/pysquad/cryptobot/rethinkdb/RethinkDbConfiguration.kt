package me.pysquad.cryptobot.rethinkdb

import me.pysquad.cryptobot.configReader

data class RethinkDbConfiguration(
        val port: Int = configReader.getInt("rethinkdb.port"),
        val host: String = configReader.getString("rethinkdb.host")
)

