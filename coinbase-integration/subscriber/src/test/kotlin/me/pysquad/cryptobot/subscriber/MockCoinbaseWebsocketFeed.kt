package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.http4k.format.Jackson.auto
import org.http4k.routing.bind
import org.http4k.routing.websockets
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.*


class MockCoinbaseWebsocketFeed(private val port: Int) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val server: Http4kServer

    init {
        val route =
            "/" bind { ws: Websocket ->
                ws.onError { throwable -> log.error("Something happened: $throwable") }
                ws.onMessage {
                    log.info("Got message: ${it.body}")
                    val productId = ProductSubscription.lens(it).productIds.first()
                    if (mockValidCoinbaseProducts.contains(productId)) {
                        ws.send(productSubscriptionResponse(productId))
                        TestData.mockCoinbaseMessages.forEach { coinbaseMessage ->
                            ws.send(coinbaseMessage.toWsMessage())
                        }
                    }
                    else
                        ws.send(invalidProductIdResponse(productId))
                }
            }

        server = websockets(route).asServer(Jetty(port))
    }

    fun start() {
        log.info("Starting MockCoinbaseWebsocketFeed server[:${port}]")
        server.start()
    }
    fun stop() {
        log.info("Stopping MockCoinbaseWebsocketFeed server...")
        server.stop()
    }
}

// internal //
private val mockValidCoinbaseProducts = listOf("BTC-EUR", "ETH-EUR", "BTC-GBP", "ETH-GBP")

private fun productSubscriptionResponse(productId: String): WsMessage =
    ProductSubscriptionResponse("subscriptions",
        listOf(Channel("ticker", listOf(productId)))
    ).toWsMessage()

private fun invalidProductIdResponse(productId: String): WsMessage =
    FailedProductSubscriptionResponse("error",
        "Failed to subscribe",
        "$productId is not a valid product"
    ).toWsMessage()

// mock coinbase ws feed models //
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
private data class ProductSubscription(val type: String, val productIds: List<String>, val channels: List<String>) {
    companion object {
        val lens = WsMessage.auto<ProductSubscription>().toLens()
    }
}
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
private data class ProductSubscriptionResponse(val type: String, val channels: List<Channel>) {

    private val lens = WsMessage.auto<ProductSubscriptionResponse>().toLens()

    fun toWsMessage(): WsMessage = lens(this)
}
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Channel(val name: String, val productIds: List<String>)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class FailedProductSubscriptionResponse(val type: String, val message: String, val reason: String) {

    private val lens = WsMessage.auto<FailedProductSubscriptionResponse>().toLens()

    fun toWsMessage(): WsMessage = lens(this)
}

private val coinbaseMessageWsLens by lazy { WsMessage.auto<CoinbaseMessage>().toLens() }
private fun CoinbaseMessage.toWsMessage() = coinbaseMessageWsLens(this)

// test data //
//WsMessage(body={"type":"ticker","sequence":11330774567,"product_id":"BTC-EUR","price":"44013.23","open_24h":"46464","volume_24h":"1997.03636012","low_24h":"43930.89","high_24h":"47733","volume_30d":"56136.26025911","best_bid":"44006.63","best_ask":"44022.03","side":"buy","time":"2021-05-12T22:10:02.497866Z","trade_id":42419162,"last_size":"0.0193818"})
private object TestData {
    val mockCoinbaseMessages by lazy {
        listOf(
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "buy", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size"),
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "buy", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size"),
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "buy", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size"),
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "buy", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size"),
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "sell", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size"),
            CoinbaseMessage("ticker", "some-sequence", "BTC-EUR", "some-price", "some-open-24h", "some-volume-24h", "some-low-24h", "some-high-24h", "some-volume-30d", "some-best-bid", "some-best-ask", "sell", Instant.now().toString(), UUID.randomUUID().toString(), "some-last-size")
        )
    }
}
