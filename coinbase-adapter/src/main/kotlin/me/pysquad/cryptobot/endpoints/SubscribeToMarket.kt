package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.Channel
import me.pysquad.cryptobot.CoinbaseMessageType.Subscribe
import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.coinbase.ProductId
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.CoinbaseSubscribeRequest
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK

class SubscribeToMarket(private val coinbase: CoinbaseApi): Endpoint {
    private val exampleSubscribeRequest =
            CoinbaseSubscribeRequest(
                    type = Subscribe,
                    productIds = listOf(ProductId("ETH-GBP")),
                    channels = listOf(Channel("ticker"))
            )

    override val contractRoute =
        "/coinbase/subscribe" meta {
            summary = "Subscribes to the real-time market data flow on orders and trades."
            receiving(CoinbaseSubscribeRequest.lens to exampleSubscribeRequest)
            returning(OK)
            security = SecurityProvider.basicAuth()
        } bindContract POST to handler()

    private fun handler(): HttpHandler = { req: Request ->
        with(CoinbaseSubscribeRequest.lens(req)) {
            coinbase.subscribe(this)
            Response(OK).body("Connecting to coinbase websocket...\n")
        }
    }
}