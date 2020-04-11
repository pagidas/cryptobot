package me.pysquad.cryptobot

import me.pysquad.cryptobot.config.ConfigReader
import org.http4k.server.Jetty
import org.http4k.server.asServer

object CryptobotApp {
    private val server = CryptobotServer.build()

    /**
     *  Here we pass all the dependencies that routes need to function.
     *  The dependencies are instantiated in [CryptobotServer.build]
     */
    operator fun invoke() =
        CryptobotRoutes(
            server.coinbase
        )
}

fun main() {
    CryptobotApp().asServer(ConfigReader.app.server.port.let(::Jetty)).start()
}