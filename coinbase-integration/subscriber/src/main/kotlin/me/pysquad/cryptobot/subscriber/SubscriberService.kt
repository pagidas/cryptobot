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

    fun createCoinbaseProductSubscription(productId: ProductId):
            Either<CoinbaseProductSubscriptionFailure, CoinbaseProductSubscriptionSuccess> {

        log.info("Subscribing to coinbase websocket feed with product id: $productId")
        if (subscriberRepository.getSubscriptions().map(CoinbaseProductSubscription::productId).contains(productId))
            return Either.left(alreadySubscribed(productId))

        coinbaseWsNonBlockingClient.onMessage { wsMessage ->
            val jsonWsLens = WsMessage.json().toLens()
            val tickerMessageLens = WsMessage.auto<CoinbaseMessage>().toLens()

            val jsonWsMessage = jsonWsLens(wsMessage)

            when(jsonWsMessage["type"].asText()) {
                CoinbaseRequestResponseTypes.ERROR.name.toLowerCase() -> {
                    val coinbaseWsResponse = CoinbaseWsResponse.wsLens(wsMessage)
                    log.error("Failed to subscribe to coinbase $coinbaseWsResponse")
                }
                CoinbaseRequestResponseTypes.SUBSCRIPTIONS.name.toLowerCase() -> {
                    val coinbaseWsResponse = CoinbaseWsResponse.wsLens(wsMessage)
                    if (coinbaseWsResponse.channels.isNotEmpty()) {
                        log.info("SUBSCRIPTION $coinbaseWsResponse") // test -- remove me
                        subscriberRepository.storeSubscription(
                            CoinbaseProductSubscription(
                                coinbaseWsResponse.channels.first().name,
                                coinbaseWsResponse.channels.first().productIds.first()
                            )
                        )
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
                "subscribe",
                listOf(productId),
                listOf("ticker")
            )
        )

        return Either.right(CoinbaseProductSubscriptionSuccess("ACCEPTED"))
    }

    private fun alreadySubscribed(productId: ProductId) = CoinbaseProductSubscriptionFailure(
        "failure",
        "Failed to subscribe",
        "Already subscribed to $productId"
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