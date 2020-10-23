package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.coinbase.CoinbaseConfiguration
import me.pysquad.cryptobot.endpoints.subscriberRoutes
import me.pysquad.cryptobot.repo.SubscriberRepo
import me.pysquad.cryptobot.rethinkdb.RethinkDbConfiguration
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource
import me.pysquad.cryptobot.service.SubscriberService

fun main() {
    val appConfig = AppConfiguration()

    http4kApp(appConfig.port) {
        // building the service
        val coinbaseConfig = CoinbaseConfiguration()
        val rethinkDbConfig = RethinkDbConfiguration()

        val service = SubscriberService(
                CoinbaseApi.client(coinbaseConfig),
                coinbaseConfig,
                SubscriberRepo.impl(RethinkDbDatasource(rethinkDbConfig))
        )

        // passing the routes
        subscriberRoutes(service)
    }.run()
}
