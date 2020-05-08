package me.pysquad.cryptobot

import me.pysquad.cryptobot.config.ConfigReader
import org.http4k.server.Jetty
import org.http4k.server.asServer

object CoinbaseAdapterApp {
    private val server = CoinbaseAdapterServer.build()

    /**
     *  Here we pass all the dependencies that routes need to function.
     *  The dependencies are instantiated in [CoinbaseAdapterServer.build]
     */
    operator fun invoke() =
        CoinbaseAdapterRoutes(
            server.coinbase
        )
}

fun main() {
    CoinbaseAdapterApp().asServer(ConfigReader.app.server.port.let(::Jetty)).start()
}