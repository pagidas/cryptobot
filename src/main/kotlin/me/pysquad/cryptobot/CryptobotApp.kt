package me.pysquad.cryptobot

import me.pysquad.cryptobot.config.ConfigReader
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    with(CryptobotRoutes()) {
        asServer(ConfigReader.app.server.port.let(::Jetty)).start()
    }
}