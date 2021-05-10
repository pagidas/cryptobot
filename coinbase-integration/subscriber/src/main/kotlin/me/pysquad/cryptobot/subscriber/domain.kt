package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

typealias ProductIds = List<ProductId>
typealias ProductId = String

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseMessage(
    val type: String,
    val sequence: String,
    val productId: String,
    val price: String,
    @field:JsonProperty("open_24h")
    val open24h: String,
    @field:JsonProperty("volume_24h")
    val volume24h: String,
    @field:JsonProperty("low_24h")
    val low24h: String,
    @field:JsonProperty("high_24h")
    val high24h: String,
    @field:JsonProperty("volume_30d")
    val volume30d: String,
    val bestBid: String,
    val bestAsk: String,
    val side: String,
    val time: String,
    val tradeId: String,
    val lastSize: String
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseProductSubscriptionV2(
    val channel: String,
    val productId: ProductId
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
