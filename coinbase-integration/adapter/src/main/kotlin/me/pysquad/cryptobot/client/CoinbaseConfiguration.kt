package me.pysquad.cryptobot.client

import me.pysquad.cryptobot.configReader
import org.http4k.core.Uri

data class CoinbaseAuthentication(val key: String, val secret: String, val passphrase: String)

data class CoinbaseConfiguration(
        val uri: Uri = Uri.of(configReader.getString("coinbase.uri")),
        val authentication: CoinbaseAuthentication =
                with(configReader.getObject("coinbase.authentication").toConfig()) {
                    CoinbaseAuthentication(
                            getString("key"),
                            getString("secret"),
                            getString("passphrase")
                    )
                }
)