package me.pysquad.cryptobot.subscriber

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import me.pysquad.cryptobot.http4k.starter.http4kApp

class Application(config: Config) {
    val appConfig = AppConfiguration(config)
    val coinbaseConfig = CoinbaseConfiguration(config)
    val rethinkDbConfig = RethinkDbConfiguration(config)

    val subscriberService = SubscriberService(
        CoinbaseWsFeedNonBlocking(coinbaseConfig),
        SubscriberRepository.impl(RethinkDbDatasource(rethinkDbConfig))
    )

    fun start() = http4kApp(appConfig.port) {
        // passing the routes
        SubscriberWebController(subscriberService).routes()
    }.run()
}

fun main() {
    Application(ConfigFactory.load()).start()
}
