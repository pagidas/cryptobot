package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.common.Http4kApp
import me.pysquad.cryptobot.config.getDbConfig
import me.pysquad.cryptobot.config.getServerPort
import me.pysquad.cryptobot.graphql.GraphQLRoute
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.routing.routes

fun buildApp() = object: Http4kApp {
    override val port = getServerPort
    override val routes = routes(
        CoinbaseAdapterRoutes(
                CoinbaseAdapterService.buildIt(),
                CoinbaseApi.buildIt(),
                SecurityProvider.buildIt()
        ),
        GraphQLRoute(CoinbaseAdapterService.buildIt())
    )
}

fun main() {
    RealTimeDb.connect(dbConfig = getDbConfig) { runMigrations() }
    buildApp().invoke()
}