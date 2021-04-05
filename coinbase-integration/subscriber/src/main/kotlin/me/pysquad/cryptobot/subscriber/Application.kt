package me.pysquad.cryptobot.subscriber

import me.pysquad.cryptobot.http4k.starter.http4kApp

fun main() {
    val appConfig = AppConfiguration()

    http4kApp(appConfig.port) {
        // building the service
        val coinbaseConfig = CoinbaseConfiguration()
        val rethinkDbConfig = RethinkDbConfiguration()

        val subscriberService = SubscriberService(
                CoinbaseApi.client(coinbaseConfig),
                SubscriberRepository.impl(RethinkDbDatasource(rethinkDbConfig))
        )

        // passing the routes
        SubscriberWebController(subscriberService).routes()
    }.run()
}
