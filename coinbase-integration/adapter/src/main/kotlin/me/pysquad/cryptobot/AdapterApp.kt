package me.pysquad.cryptobot

import me.pysquad.cryptobot.client.CoinbaseApi
import me.pysquad.cryptobot.client.CoinbaseConfiguration
import me.pysquad.cryptobot.coinbaseprofiles.service.ProfilesService

fun main() {
    val appConfig = AppConfiguration()

    http4kApp(appConfig.port) {
        // build the service
        val coinbaseConfig = CoinbaseConfiguration()

        val service = ProfilesService(
                CoinbaseApi.client(coinbaseConfig)
        )

        // passing the routes
        adapterRoutes(service)
    }.run()
}