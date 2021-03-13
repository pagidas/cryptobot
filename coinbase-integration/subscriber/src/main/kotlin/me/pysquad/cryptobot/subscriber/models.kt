package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SubscriberResponse(
    val type: String,
    val message: String,
    val reason: String,
    val data: ProductIds
) {
    companion object {
        val lens = Body.auto<SubscriberResponse>().toLens()

        fun success(type: String, message: String, data: ProductIds) =
            SubscriberResponse(type = type, message = message, data = data, reason = "")

        fun failure(type: String, message: String, reason: String) =
            SubscriberResponse(type = type, message = message, reason = reason, data = emptyList())
    }
}

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseWsSubscribeRequest(
    val type: String,
    val productIds: ProductIds,
    val channels: List<String>
)

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