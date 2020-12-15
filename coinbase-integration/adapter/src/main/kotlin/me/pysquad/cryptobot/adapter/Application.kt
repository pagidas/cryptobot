package me.pysquad.cryptobot.adapter

import me.pysquad.cryptobot.adapter.client.CoinbaseApi
import me.pysquad.cryptobot.adapter.client.CoinbaseConfiguration
import me.pysquad.cryptobot.adapter.coinbaseprofiles.service.ProfilesService
import me.pysquad.cryptobot.http4k.starter.http4kApp

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