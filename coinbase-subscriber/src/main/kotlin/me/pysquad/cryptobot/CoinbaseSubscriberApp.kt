package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.coinbase.CoinbaseConfiguration
import me.pysquad.cryptobot.common.Http4kApp
import me.pysquad.cryptobot.endpoints.CoinbaseSubscriberRoutes
import me.pysquad.cryptobot.repo.CoinbaseSubscriberRepo
import me.pysquad.cryptobot.rethinkdb.RethinkDbConfiguration
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource
import me.pysquad.cryptobot.service.CoinbaseSubscriberService

fun buildApp(): Http4kApp {
    val appConfig = AppConfiguration()
    val coinbaseConfig = CoinbaseConfiguration()
    val rethinkDbConfig = RethinkDbConfiguration()

    return object: Http4kApp {
        override val port: Int = appConfig.port
        override val routes = CoinbaseSubscriberRoutes(
                CoinbaseSubscriberService(
                        CoinbaseApi.client(coinbaseConfig),
                        coinbaseConfig,
                        CoinbaseSubscriberRepo.impl(RethinkDbDatasource(rethinkDbConfig))
                )
        )
    }
}

fun main() {
    buildApp().run()
}