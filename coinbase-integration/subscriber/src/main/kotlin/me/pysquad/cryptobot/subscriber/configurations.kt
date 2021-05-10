package me.pysquad.cryptobot.subscriber

import com.typesafe.config.Config
import org.http4k.core.Uri

class AppConfiguration(config: Config) {
        val port: Int = config.getInt("application.port")
        val host: String = config.getString("application.host")
}

class RethinkDbConfiguration(config: Config) {
        val port: Int = config.getInt("rethinkdb.port")
        val host: String = config.getString("rethinkdb.host")
}

class CoinbaseConfiguration(config: Config) {
        val wsFeedUri: Uri = Uri.of(config.getString("coinbase.wsFeedUri"))
}