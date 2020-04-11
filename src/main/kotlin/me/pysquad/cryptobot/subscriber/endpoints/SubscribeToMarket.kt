package me.pysquad.cryptobot.subscriber.endpoints

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.subscriber.CoinbaseSubscribeRequest
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

class SubscribeToMarket(val coinbase: CoinbaseApi): Endpoint {
    override val contractRoute =
        "/market/subscribe" meta {
            summary = "Subscribes to the real-time market data flow on orders and trades."
        } bindContract POST to handler()

    private fun handler(): HttpHandler = { req: Request ->
        with(CoinbaseSubscribeRequest.lens(req)) {
            coinbase.subscribe(this)
            Response(OK)
        }
    }
}