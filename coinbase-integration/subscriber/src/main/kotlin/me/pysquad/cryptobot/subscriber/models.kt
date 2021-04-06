package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

data class CoinbaseProductSubscriptionSuccess(val productIds: ProductIds) {
    companion object {
        val lens = Body.auto<CoinbaseProductSubscriptionSuccess>().toLens()
    }
}

data class CoinbaseProductSubscriptionFailure(
    val type: String,
    val message: String,
    val reason: String
) {
    companion object {
        val lens = Body.auto<CoinbaseProductSubscriptionFailure>().toLens()
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseWsSubscribeRequest(
    val type: String,
    val productIds: ProductIds,
    val channels: List<String>
)

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
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
