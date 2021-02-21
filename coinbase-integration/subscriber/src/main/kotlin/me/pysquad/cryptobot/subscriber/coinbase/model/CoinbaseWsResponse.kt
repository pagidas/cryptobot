package me.pysquad.cryptobot.subscriber.coinbase.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import me.pysquad.cryptobot.subscriber.model.ProductIds

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseWsResponse(
        var type: String = "",
        var message: String = "",
        var reason: String = "",
        var channels: List<CoinbaseWsChannel> = emptyList()
) {
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
    data class CoinbaseWsChannel(
            var name: String = "",
            var productIds: ProductIds = emptyList()
    )
}