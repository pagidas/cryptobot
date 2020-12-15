package me.pysquad.cryptobot.subscriber

import me.pysquad.cryptobot.http4k.starter.http4kApp
import me.pysquad.cryptobot.subscriber.coinbase.CoinbaseApi
import me.pysquad.cryptobot.subscriber.coinbase.CoinbaseConfiguration
import me.pysquad.cryptobot.subscriber.endpoints.subscriberRoutes
import me.pysquad.cryptobot.subscriber.repo.SubscriberRepo
import me.pysquad.cryptobot.subscriber.rethinkdb.RethinkDbConfiguration
import me.pysquad.cryptobot.subscriber.rethinkdb.RethinkDbDatasource
import me.pysquad.cryptobot.subscriber.service.SubscriberService

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
