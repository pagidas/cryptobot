package me.pysquad.cryptobot.subscriber

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import me.pysquad.cryptobot.http4k.starter.http4kApp

class Application {
    companion object {
        fun start(config: Config) {
            val appConfig = AppConfiguration(config)
            val coinbaseConfig = CoinbaseConfiguration(config)
            val rethinkDbConfig = RethinkDbConfiguration(config)

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
    }
}

fun main() {
    Application.start(ConfigFactory.load())
}
