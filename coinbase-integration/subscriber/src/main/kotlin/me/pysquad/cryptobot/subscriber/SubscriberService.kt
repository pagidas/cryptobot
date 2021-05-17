package me.pysquad.cryptobot.subscriber

import io.vavr.control.Either
import org.http4k.format.Jackson.auto
import org.http4k.format.Jackson.json
import org.http4k.websocket.WsMessage
import org.slf4j.LoggerFactory

class SubscriberService(
    private val coinbaseWsNonBlockingClient: CoinbaseWsFeedNonBlocking,
    private val subscriberRepository: SubscriberRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    /*
    In-memory store of subscriptions for quick-access by async websocket client.
    It's faster to check the subscription than connecting to RethinkDb.
    However, the subscription is still stored in RethinkDb for consumers that need it.
     */
    val subscriptions = ArrayList<CoinbaseProductSubscription>()

    fun createCoinbaseProductSubscription(productId: ProductId):
            Either<CoinbaseProductSubscriptionFailure, CoinbaseProductSubscriptionSuccess> {

        log.info("Subscribing to coinbase websocket feed with product id: $productId")
        if (subscriptions.isNotEmpty()) return Either.left(alreadySubscribed(productId))

        val jsonWsLens = WsMessage.json().toLens()
        val tickerMessageLens = WsMessage.auto<CoinbaseMessage>().toLens()

        coinbaseWsNonBlockingClient.onMessage { wsMessage ->
            val jsonWsMessage = jsonWsLens(wsMessage)

            when(jsonWsMessage["type"].asText()) {
                CoinbaseRequestResponseTypes.ERROR.name.toLowerCase() -> {
                    val coinbaseWsResponse = CoinbaseWsResponse.wsLens(wsMessage)
                    log.error("Failed to subscribe to coinbase $coinbaseWsResponse")
                }
                CoinbaseRequestResponseTypes.SUBSCRIPTIONS.name.toLowerCase() -> {
                    val coinbaseWsResponse = CoinbaseWsResponse.wsLens(wsMessage)
                    if (coinbaseWsResponse.channels.isNotEmpty() && subscriptions.isEmpty()) {
                        log.info("SUBSCRIPTION $coinbaseWsResponse") // test -- remove me
                        val subscription = CoinbaseProductSubscription(
                            coinbaseWsResponse.channels.first().name,
                            coinbaseWsResponse.channels.first().productIds.first())
                        subscriptions.add(subscription)
                        subscriberRepository.storeSubscription(subscription)
                    }
                }
                CoinbaseChannelTypes.TICKER.name.toLowerCase() -> {
                    val coinbaseMessage = tickerMessageLens(wsMessage)
                    subscriberRepository.storeMessage(coinbaseMessage)
                }
                else -> log.info("UNKNOWN message: ${CoinbaseWsResponse.wsLens(wsMessage)}")
            }
        }

        coinbaseWsNonBlockingClient.createProductSubscription(
            CoinbaseWsSubscribeRequest(
                CoinbaseRequestResponseTypes.SUBSCRIBE.name.toLowerCase(),
                listOf(productId),
                listOf("ticker")
            )
        )

        return Either.right(CoinbaseProductSubscriptionSuccess("ACCEPTED"))
    }

    private fun alreadySubscribed(productId: ProductId) = CoinbaseProductSubscriptionFailure(
        "failure",
        "Failed to subscribe",
        "Already subscribed once to $productId"
    )
}

// internal models
enum class CoinbaseRequestResponseTypes {
    SUBSCRIPTIONS,
    SUBSCRIBE,
    ERROR
}

enum class CoinbaseChannelTypes {
    TICKER
}