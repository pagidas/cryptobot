package me.pysquad.cryptobot.subscriber

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.http4k.core.Uri

private val configReader: Config by lazy { ConfigFactory.load() }

data class AppConfiguration(
        val port: Int = configReader.getInt("application.port"),
        val host: String = configReader.getString("application.host")
)

data class RethinkDbConfiguration(
        val port: Int = configReader.getInt("rethinkdb.port"),
        val host: String = configReader.getString("rethinkdb.host")
)

data class CoinbaseConfiguration(
        val wsFeedUri: Uri = Uri.of(configReader.getString("coinbase.wsFeedUri")),
        val subscribeRequest: Config = configReader.getObject("coinbase.subscribeRequest").toConfig()
)