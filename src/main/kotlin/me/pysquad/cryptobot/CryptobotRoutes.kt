package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.subscriber.endpoints.SubscribeToMarket
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

object CryptobotRoutes {
    operator fun invoke(coinbase: CoinbaseApi): RoutingHttpHandler {
        val contractRoutes =
            listOf(
                // --- Subscriber ---
                SubscribeToMarket(coinbase)
            )

        val contract = contract {
            renderer = OpenApi3(ApiInfo("Cryptobot API", "v1.0"), Jackson)
            descriptionPath = "/swagger.json"
            routes += contractRoutes.map { it.contractRoute }
        }

        return ServerFilters.CatchLensFailure
            .then(DebuggingFilters.PrintRequestAndResponse())
            .then(routes("api" bind contract))
    }
}