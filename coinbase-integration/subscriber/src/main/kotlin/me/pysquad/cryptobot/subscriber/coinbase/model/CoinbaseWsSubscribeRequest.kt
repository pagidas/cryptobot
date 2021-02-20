package me.pysquad.cryptobot.subscriber.coinbase.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.subscriber.model.ProductIds

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseWsSubscribeRequest(
        val type: String,
        val productIds: ProductIds,
        val channels: List<String>
)