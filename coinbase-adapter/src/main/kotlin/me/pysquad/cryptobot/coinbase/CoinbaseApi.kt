package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.CoinbaseAdapterRepoImpl
import me.pysquad.cryptobot.CoinbaseAdapterRepository
import me.pysquad.cryptobot.CoinbaseSubscribeRequest
import me.pysquad.cryptobot.RealTimeDb
import me.pysquad.cryptobot.config.ConfigReader
import me.pysquad.cryptobot.security.SecurityProvider
import me.pysquad.cryptobot.security.SecurityProvider.Companion.hmacSHA256
import org.apache.commons.codec.binary.Base64
import org.http4k.client.ApacheClient
import org.http4k.client.WebsocketClient
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Uri
import org.http4k.websocket.WsMessage
import java.time.Instant
import org.http4k.format.Jackson.json
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
                        val wsJsonLens = WsMessage.json().toLens()

                        send(WsMessage(subscribeRequest.toNativeCoinbaseRequest()))
                        val firstMessage = received().first().apply(::println)

                        if (wsJsonLens(firstMessage)["type"].asText() != "error") {
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
                    coinbaseRepo.store(
                        listOfMessages.map { CoinbaseProductMessage.fromWsMessage(it) }
                    )
                }
            }

            fun Request.asCoinbaseSandboxAuthenticated(): Request {
                val timestamp = String.format("%.3f", Instant.now().toEpochMilli() / 1000.0)
                val coinbaseSandbox = securityProvider.getCoinbaseSandboxApiCredentials()

                // build the message
                val message = (timestamp + method.name + uri.path + bodyString()).toByteArray()

                // base64 decode the secret
                val secret = Base64.decodeBase64(coinbaseSandbox.secret)

                // sign the message with the hmac key
                val signature = hmacSHA256(message, secret)

                // finally base64 encode the result
                val encodedSignature = String(Base64.encodeBase64(signature))

                return headers(listOf(
                        "CB-ACCESS-KEY" to coinbaseSandbox.key,
                        "CB-ACCESS-SIGN" to encodedSignature,
                        "CB-ACCESS-TIMESTAMP" to timestamp,
                        "CB-ACCESS-PASSPHRASE" to coinbaseSandbox.passphrase
                ))
            }

        }
    }
}
