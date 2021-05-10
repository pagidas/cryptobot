package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.WebsocketClient
import org.http4k.websocket.Websocket
import org.http4k.websocket.WsMessage
import org.http4k.websocket.WsStatus
import org.slf4j.LoggerFactory

class CoinbaseWsFeedNonBlocking(config: CoinbaseConfiguration) {

    private val log = LoggerFactory.getLogger(this::class.java)
    private val objectMapper = jacksonObjectMapper().registerModule(KotlinModule())

    private val nonBlockingClient: Websocket = WebsocketClient.nonBlocking(config.wsFeedUri) { ws ->
        ws.onClose {
            log.error("Closing coinbase websocket connection with status: {}, and reason: {}", it.code, it.description)
        }
        ws.onError {
            log.error("Error in coinbase websocket connection: {}", it)
        }
    }

    fun onMessage(fn: (WsMessage) -> Unit) = nonBlockingClient.onMessage(fn)

    fun close(status: WsStatus): Unit = nonBlockingClient.close(status)

    fun createProductSubscription(request: CoinbaseWsSubscribeRequest) {
        println(nonBlockingClient.upgradeRequest)
        val wsRequest = WsMessage(objectMapper.writeValueAsString(request))
        nonBlockingClient.send(wsRequest)
    }
}
