package me.pysquad.cryptobot.subscriber

import com.typesafe.config.ConfigFactory
import me.pysquad.cryptobot.http4k.starter.http4kApp

fun main() {
    val configReader = ConfigFactory.load()

    val appConfig = AppConfiguration(configReader)
    val coinbaseConfig = CoinbaseConfiguration(configReader)
    val rethinkDbConfig = RethinkDbConfiguration(configReader)

    http4kApp(appConfig.port) {
        // building the service
        val subscriberService = SubscriberService(
            CoinbaseWsFeedNonBlocking(coinbaseConfig),
            SubscriberRepository.impl(RethinkDbDatasource(rethinkDbConfig))
        )

        // passing the routes
        SubscriberWebController(subscriberService).routes()
    }.run()
}
