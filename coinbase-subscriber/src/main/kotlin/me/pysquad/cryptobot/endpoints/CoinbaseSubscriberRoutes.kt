package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.service.CoinbaseSubscriberService
import org.http4k.contract.contract
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes

object CoinbaseSubscriberRoutes {
    operator fun invoke(subscriberService: CoinbaseSubscriberService): RoutingHttpHandler {
        val coinbaseSubscriberContract = contract {
            routes += subscribe(subscriberService)
        }

        return ServerFilters.CatchLensFailure
                .then(DebuggingFilters.PrintRequestAndResponse())
                .then(routes(coinbaseSubscriberContract))
    }
}