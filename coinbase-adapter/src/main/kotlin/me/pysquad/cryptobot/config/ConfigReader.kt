package me.pysquad.cryptobot.config

import com.typesafe.config.ConfigFactory

data class ServerConfig(val host: String, val port: Int)
data class AppConfig(val server: ServerConfig)
data class CoinbaseConfig(val wsFeed: String, val sandboxUri: String)
data class DbConfig(val host: String, val port: Int)

object ConfigReader {
    private val config = ConfigFactory.load()

    val app = AppConfig(
        ServerConfig(host = config.getString("server.host"), port = config.getInt("server.port"))
    )

    val coinbase = CoinbaseConfig(
        wsFeed = config.getString("coinbase.wsFeed"),
        sandboxUri = config.getString("coinbase.sandboxUri")
    )

    val db = DbConfig(
        host = config.getString("db.host"),
        port = config.getInt("db.port")
    )
}

val getServerPort = ConfigReader.app.server.port
val getDbConfig = ConfigReader.db