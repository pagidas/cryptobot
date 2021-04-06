package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.WebsocketClient
import org.http4k.format.Jackson.auto
import org.http4k.websocket.WsMessage
import org.slf4j.LoggerFactory

interface CoinbaseWsApi {
    fun createProductSubscription(request: CoinbaseWsSubscribeRequest): CoinbaseWsResponse
    fun getReceived(): Sequence<WsMessage>
    fun close()

    companion object {
        fun client(coinbaseConfig: CoinbaseConfiguration) = object: CoinbaseWsApi {

            private val log = LoggerFactory.getLogger(this::class.java)
            private val objectMapper = jacksonObjectMapper().registerModule(KotlinModule())
            private val wsClient = WebsocketClient.blocking(coinbaseConfig.wsFeedUri)

            override fun createProductSubscription(request: CoinbaseWsSubscribeRequest): CoinbaseWsResponse {
                val wsFeedLens = WsMessage.auto<CoinbaseWsResponse>().toLens()
                val wsRequest = WsMessage(objectMapper.writeValueAsString(request))

                return with(wsClient) {
                    send(wsRequest)
                    // FIXME: 06/04/2021 received() will not guarantee we're getting the result we're expecting.
                    val first = received().take(1).first()
                    log.debug("Got raw response from coinbase websocket feed: ${first.bodyString()}")
                    val wsFeedResp = wsFeedLens.extract(first)
                    log.debug("Got response from coinbase websocket feed: $wsFeedResp")
                    wsFeedResp
                }
            }

            override fun getReceived(): Sequence<WsMessage> = wsClient.received()

            override fun close() = wsClient.close()
        }
    }
}
