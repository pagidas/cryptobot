package me.pysquad.cryptobot.subscriber.coinbase

import com.typesafe.config.Config
import me.pysquad.cryptobot.subscriber.configReader
import org.http4k.core.Uri

data class CoinbaseConfiguration(
        val wsFeedUri: Uri = Uri.of(configReader.getString("coinbase.wsFeedUri")),
        val subscribeRequest: Config = configReader.getObject("coinbase.subscribeRequest").toConfig()
)