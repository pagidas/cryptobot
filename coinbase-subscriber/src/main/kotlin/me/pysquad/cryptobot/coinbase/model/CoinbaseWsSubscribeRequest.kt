package me.pysquad.cryptobot.coinbase.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.model.ProductIds

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class CoinbaseWsSubscribeRequest(
        val type: String,
        val productIds: ProductIds,
        val channels: List<String>
)