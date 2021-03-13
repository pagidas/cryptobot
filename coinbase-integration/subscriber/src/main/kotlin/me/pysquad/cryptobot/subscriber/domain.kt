package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

typealias ProductIds = List<String>

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseMessage(
    var type: String = "",
    var sequence: String = "",
    var productId: String = "",
    var price: String = "",
    @field:JsonProperty("open_24h")
    var open24h: String = "",
    @field:JsonProperty("volume_24h")
    var volume24h: String = "",
    @field:JsonProperty("low_24h")
    var low24h: String = "",
    @field:JsonProperty("high_24h")
    var high24h: String = "",
    @field:JsonProperty("volume_30d")
    var volume30d: String = "",
    var bestBid: String = "",
    var bestAsk: String = "",
    var side: String = "",
    var time: String = "",
    var tradeId: String = "",
    var lastSize: String = ""
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseProductSubscription(
    var subId: String = "",
    var productIds: String = ""
) {
    val productIdsAsList: ProductIds
        @JsonIgnore
        get() =
            if (productIds.isBlank())
                emptyList()
            else {
                productIds.split(",").toMutableList()
                    .apply { removeAll { it.isBlank() } }
                    .toList()
            }
}
