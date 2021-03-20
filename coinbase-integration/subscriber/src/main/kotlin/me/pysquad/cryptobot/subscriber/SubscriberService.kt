package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.format.Jackson.auto
import org.http4k.lens.LensFailure
import org.http4k.lens.LensFailures
import org.http4k.websocket.WsMessage
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class SubscriberService(
    private val coinbaseApi: CoinbaseApi,
    private val coinbaseConfig: CoinbaseConfiguration,
    private val subscriberRepository: SubscriberRepository
) {

    private val objectMapper = jacksonObjectMapper().registerModule(KotlinModule())
    private val logger = LoggerFactory.getLogger(SubscriberService::class.java)

    fun subscribe(productIds: ProductIds): SubscriberResponse {
        logger.info("Subscribing to coinbase websocket feed with products: $productIds")
        val request = CoinbaseWsSubscribeRequest(
                type = coinbaseConfig.subscribeRequest.getString("type"),
                channels = coinbaseConfig.subscribeRequest.getStringList("channels"),
                productIds = productIds
        ).run(objectMapper::writeValueAsString)

        with(coinbaseApi.wsFeed()) {
            val wsFeedLens = WsMessage.auto<CoinbaseWsResponse>().toLens()

            send(WsMessage(request))
            val wsFeedResp = wsFeedLens.extract(received().first())
            logger.debug("Got response from coinbase websocket feed: $wsFeedResp")

            return if (wsFeedResp.type != "error") {
                // right now we only send one channel, which is a 'ticker' channel
                subscriberRepository.storeSubscriptions(wsFeedResp.channels.first().productIds)
                thread { received().storeInChunks() }

                SubscriberResponse.success(
                        wsFeedResp.type,
                        "Successfully subscribed to coinbase pro websocket feed",
                        wsFeedResp.channels.first().productIds
                )
            } else {
                // closing websocket client
                logger.info("Closing websocket connection with coinbase")
                close()

                SubscriberResponse.failure(
                        wsFeedResp.type,
                        wsFeedResp.message,
                        wsFeedResp.reason
                )
            }
        }
    }

    private fun Sequence<WsMessage>.storeInChunks() {
        val wsLens = WsMessage.auto<CoinbaseMessage>().toLens()

        for(listOfMessages in chunked(6)) {
            val coinbaseMessages = listOfMessages.map(wsLens::extract)
            subscriberRepository.storeMessages(coinbaseMessages)
        }
    }
}

// internal models
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
private data class CoinbaseWsSubscribeRequest(
    val type: String,
    val productIds: ProductIds,
    val channels: List<String>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
private data class CoinbaseWsResponse(
    var type: String = "",
    var message: String = "",
    var reason: String = "",
    var channels: List<CoinbaseWsChannel> = emptyList()
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class CoinbaseWsChannel(
        var name: String = "",
        var productIds: ProductIds = emptyList()
    )
}