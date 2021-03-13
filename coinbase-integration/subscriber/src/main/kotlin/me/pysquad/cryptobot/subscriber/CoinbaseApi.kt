package me.pysquad.cryptobot.subscriber

import org.http4k.client.WebsocketClient
import org.http4k.websocket.WsClient

interface CoinbaseApi {
    fun wsFeed(): WsClient

    companion object {
        fun client(coinbaseConfig: CoinbaseConfiguration) = object: CoinbaseApi {
            override fun wsFeed(): WsClient = WebsocketClient.blocking(coinbaseConfig.wsFeedUri)
        }
    }
}