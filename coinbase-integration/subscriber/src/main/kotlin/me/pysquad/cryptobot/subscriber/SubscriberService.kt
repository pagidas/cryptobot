package me.pysquad.cryptobot.subscriber

import io.vavr.control.Either
import org.http4k.format.Jackson.auto
import org.http4k.websocket.WsMessage
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class SubscriberService(
    private val coinbaseWsClient: CoinbaseWsApi,
    private val subscriberRepository: SubscriberRepository
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun createCoinbaseProductSubscription(productIds: ProductIds):
            Either<CoinbaseProductSubscriptionFailure, CoinbaseProductSubscriptionSuccess> {

        log.info("Subscribing to coinbase websocket feed with products: $productIds")
        val coinbaseProductSubscriptions = subscriberRepository.getSubscriptions()
        if (productIds.any { given -> coinbaseProductSubscriptions.contains(given) })
            return Either.left(alreadySubscribed(productIds))

        val response = coinbaseWsClient.createProductSubscription(
            CoinbaseWsSubscribeRequest(
                type = CoinbaseRequestResponseTypes.SUBSCRIBE.name.toLowerCase(),
                channels = listOf(CoinbaseChannelTypes.TICKER.name.toLowerCase()),
                productIds = productIds))

        return if (!response.type.equals(CoinbaseRequestResponseTypes.ERROR.name, true) && response.channels.isNotEmpty()) {
            // right now we only send one channel, which is a 'ticker' channel
            subscriberRepository.storeSubscriptions(response.channels.first().productIds)
            // new thread to store received ticker messages from websocket
            thread { coinbaseWsClient.getReceived().storeInChunks() }

            Either.right(CoinbaseProductSubscriptionSuccess(response.channels.first().productIds))
        } else {
            Either.left(CoinbaseProductSubscriptionFailure(response.type, response.message, response.reason))
        }
    }

    private fun Sequence<WsMessage>.storeInChunks() {
        val wsLens = WsMessage.auto<CoinbaseMessage>().toLens()

        for(listOfMessages in chunked(6)) {
            val coinbaseMessages = listOfMessages
                .map(wsLens::extract)
                .filter { it.type.equals(CoinbaseChannelTypes.TICKER.name, true) }
            subscriberRepository.storeMessages(coinbaseMessages)
        }
    }

    private fun alreadySubscribed(productIds: ProductIds) = CoinbaseProductSubscriptionFailure(
        "failure",
        "Failed to subscribe",
        "Already subscribed to $productIds"
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