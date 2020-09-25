package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.config.ConfigReader
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.client.ApacheClient
import org.http4k.core.Method.GET
import org.http4k.core.Request

val coinbaseSandboxUri = ConfigReader.coinbase.sandboxUri

interface CoinbaseApi {
    fun getSandboxCoinbaseProfiles(): List<GetSandboxCoinbaseProfileMessage>

    companion object {

        fun buildIt() = Client(
            SecurityProvider.buildIt()
        )

        fun Client(securityProvider: SecurityProvider) = object: CoinbaseApi {

            override fun getSandboxCoinbaseProfiles(): List<GetSandboxCoinbaseProfileMessage> = with(ApacheClient()) {
                GetSandboxCoinbaseProfileMessage.toListOfMessages(
                        this(Request(GET, "$coinbaseSandboxUri/profiles").asCoinbaseSandboxAuthenticated())
                )
            }

            private fun Request.asCoinbaseSandboxAuthenticated() = securityProvider.coinbaseSandboxHeaders(this)
        }
    }
}