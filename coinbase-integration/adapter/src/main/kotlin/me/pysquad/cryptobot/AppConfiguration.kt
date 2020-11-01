package me.pysquad.cryptobot

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

val configReader: Config = ConfigFactory.load()

data class AppConfiguration(
        val port: Int = configReader.getInt("application.port"),
        val host: String = configReader.getString("application.host")
)