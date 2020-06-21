package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.CoinbaseAdapterRepoImpl
import me.pysquad.cryptobot.CoinbaseAdapterRepository
import me.pysquad.cryptobot.CoinbaseSubscribeRequest
import me.pysquad.cryptobot.RealTimeDb
import me.pysquad.cryptobot.config.ConfigReader
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.client.ApacheClient
import org.http4k.client.WebsocketClient
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.format.Jackson.json
import org.http4k.websocket.WsMessage
import kotlin.concurrent.thread

val coinbaseWsFeedUri = Uri.of(ConfigReader.coinbase.wsFeed)
val coinbaseSandboxUri = ConfigReader.coinbase.sandboxUri

interface CoinbaseApi {
    fun subscribe(subscribeRequest: CoinbaseSubscribeRequest): CoinbaseWsFeedResponse
    fun getSandboxCoinbaseProfiles(): List<GetSandboxCoinbaseProfileMessage>

    companion object {

        fun buildIt() = Client(
            CoinbaseAdapterRepoImpl(RealTimeDb),
            SecurityProvider.buildIt()
        )

        fun Client(coinbaseRepo: CoinbaseAdapterRepository, securityProvider: SecurityProvider) = object: CoinbaseApi {

            override fun subscribe(subscribeRequest: CoinbaseSubscribeRequest): CoinbaseWsFeedResponse =
                    with(WebsocketClient.blocking(coinbaseWsFeedUri)) {

                        send(WsMessage(subscribeRequest.toNativeCoinbaseRequest()))
                        val firstMessage = received().first().apply(::println)

                        if (wsFeedHasNoError(firstMessage)) {
                            coinbaseRepo.storeSubscriptions(mapProductIds(firstMessage))
                            thread { received().storeInChunks() }
                            CoinbaseWsFeedResponse.success(firstMessage).apply(::println)
                        }
                        else {
                            close()
                            CoinbaseWsFeedResponse.error(firstMessage).apply(::println)
                        }
                    }

            override fun getSandboxCoinbaseProfiles(): List<GetSandboxCoinbaseProfileMessage> = with(ApacheClient()) {
                GetSandboxCoinbaseProfileMessage.toListOfMessages(
                        this(Request(GET, "$coinbaseSandboxUri/profiles").asCoinbaseSandboxAuthenticated())
                )
            }

            private fun Sequence<WsMessage>.storeInChunks(sizeOfChunk: Int = 6) {
                for (listOfMessages in chunked(sizeOfChunk)) {
                    listOfMessages.forEach(::println)
                    coinbaseRepo.storeMessages(
                        listOfMessages.map { CoinbaseProductMessage.fromWsMessage(it) }
                    )
                }
            }

            private fun Request.asCoinbaseSandboxAuthenticated() = securityProvider.coinbaseSandboxHeaders(this)
        }
    }
}

private val wsJsonLens = WsMessage.json().toLens()

private val wsFeedHasNoError: (WsMessage) -> Boolean = { wsMessage ->
    wsJsonLens(wsMessage)["type"].asText() != "error"
}

private val mapProductIds: (WsMessage) -> ProductsIds = { wsMessage ->
    wsJsonLens(wsMessage)["channels"]
            .asIterable().iterator().next()["product_ids"]
            .asIterable().map { ProductId(it.asText()) }
}
