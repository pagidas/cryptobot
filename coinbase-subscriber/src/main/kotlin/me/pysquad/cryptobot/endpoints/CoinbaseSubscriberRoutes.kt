package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.service.CoinbaseSubscriberService
import org.http4k.contract.contract
import org.http4k.core.HttpTransaction
import org.http4k.core.then
import org.http4k.filter.ResponseFilters
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.routes
import org.slf4j.LoggerFactory

object CoinbaseSubscriberRoutes {
    operator fun invoke(subscriberService: CoinbaseSubscriberService): RoutingHttpHandler {
        val logger = LoggerFactory.getLogger(CoinbaseSubscriberRoutes::class.java)

        val coinbaseSubscriberContract = contract {
            routes += subscribe(subscriberService)
        }

        return ResponseFilters.ReportHttpTransaction { tx: HttpTransaction ->
            logger.debug("${tx.request.method} to ${tx.request.uri} returned ${tx.response.status} and took ${tx.duration.toMillis()}.")
        }.then(routes(coinbaseSubscriberContract))
    }
}