package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.Channel
import me.pysquad.cryptobot.CoinbaseMessageType.ERROR
import me.pysquad.cryptobot.CoinbaseMessageType.SUBSCRIBE
import me.pysquad.cryptobot.CoinbaseMessageType.SUBSCRIPTIONS
import me.pysquad.cryptobot.CoinbaseSubscribeRequest
import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.coinbase.CoinbaseWsFeedError
import me.pysquad.cryptobot.coinbase.CoinbaseWsFeedResponse
import me.pysquad.cryptobot.coinbase.CoinbaseWsFeedSuccess
import me.pysquad.cryptobot.coinbase.ProductId
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

class SubscribeToMarket(private val coinbase: CoinbaseApi): Endpoint {
    private val exampleSubscribeRequest =
            CoinbaseSubscribeRequest(
                    type = SUBSCRIBE,
                    productIds = listOf(ProductId("ETH-GBP")),
                    channels = listOf(Channel("ticker"))
            )

    private val exampleSuccessCoinbaseWsFeedResponse =
            CoinbaseWsFeedSuccess(
                    type = SUBSCRIPTIONS,
                    message = "Success",
                    productIds = listOf("BTC-EUR")
            )

    private val exampleErrorCoinbaseWsFeedResponse =
            CoinbaseWsFeedError(
                    type = ERROR,
                    message = "Failed to subscribe",
                    reason = "BTasdC-EUR is not a valid product"
            )

    override val contractRoute =
        "/coinbase/wsFeed/subscribe" meta {
            summary = "Subscribes to the real-time market data flow on orders and trades."
            receiving(CoinbaseSubscribeRequest.lens to exampleSubscribeRequest)
            returning(OK, CoinbaseWsFeedResponse.successLens to exampleSuccessCoinbaseWsFeedResponse)
            returning(OK, CoinbaseWsFeedResponse.errorLens to exampleErrorCoinbaseWsFeedResponse)
            security = SecurityProvider.basicAuth()
        } bindContract POST to handler()

    private fun handler(): HttpHandler = { req: Request ->
        with(CoinbaseSubscribeRequest.lens(req)) {
            when(val resp = coinbase.subscribe(this)) {
                is CoinbaseWsFeedSuccess -> Response(OK).with(CoinbaseWsFeedResponse.successLens of resp)
                is CoinbaseWsFeedError -> Response(OK).with(CoinbaseWsFeedResponse.errorLens of resp)
                else -> Response(OK)
            }
        }
    }
}