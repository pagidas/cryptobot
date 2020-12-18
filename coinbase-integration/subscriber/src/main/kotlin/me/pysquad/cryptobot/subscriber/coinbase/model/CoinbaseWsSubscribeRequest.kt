package me.pysquad.cryptobot.subscriber.coinbase.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.subscriber.model.ProductIds

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class CoinbaseWsSubscribeRequest(
        val type: String,
        val productIds: ProductIds,
        val channels: List<String>
)