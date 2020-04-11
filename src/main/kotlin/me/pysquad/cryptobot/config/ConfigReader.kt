package me.pysquad.cryptobot.config

import com.typesafe.config.ConfigFactory

data class ServerConfig(val host: String, val port: Int)
data class AppConfig(val server: ServerConfig)
data class CoinbaseConfig(val uri: String)

object ConfigReader {
    private val config = ConfigFactory.load()

    val app = AppConfig(
        ServerConfig(host = config.getString("server.host"), port = config.getInt("server.port"))
    )

    val coinbase = CoinbaseConfig(
        uri = config.getString("coinbase.uri")
    )
}