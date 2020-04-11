package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.config.ConfigReader
import me.pysquad.cryptobot.subscriber.CoinbaseSubscribeRequest
import org.http4k.client.WebsocketClient
import org.http4k.core.Uri
import org.http4k.format.Jackson
import org.http4k.websocket.WsMessage

val coinbaseUri = Uri.of(ConfigReader.coinbase.uri)

interface CoinbaseApi {
    fun subscribe(subscribeRequest: CoinbaseSubscribeRequest)

    companion object {

        fun Client() = object: CoinbaseApi {
            override fun subscribe(subscribeRequest: CoinbaseSubscribeRequest) =
                with(WebsocketClient.blocking(coinbaseUri)) {
                    send(WsMessage(subscribeRequest.toJsonPrettyString()))
                    received().printInChunks()
                }
        }
    }
}

private fun CoinbaseSubscribeRequest.toJsonPrettyString() = Jackson {
    val productIdsJson = productIds.map { string(it.value) }
    val channelsJson = channels.map { string(it.value) }
    obj(
        "type" to type.name.toLowerCase().asJsonValue(),
        "product_ids" to array(productIdsJson),
        "channels" to array(channelsJson)
    ).toPrettyString()
}

/**
 * Helper function to print received messaes in chunks.
 * The size of chunks is by default 6.
 *
 * Similar to this function we could have a "writeInChunks"
 * when we'll use a real-time db to store those messages.
 */
private fun Sequence<WsMessage>.printInChunks(sizeOfChunk: Int = 6) {
    for (listOfMessages in chunked(sizeOfChunk)) {
        listOfMessages.forEach(::println)
        println("\nReceiving and printing next chunk of size $sizeOfChunk... be patient!\n")
    }
}
