package me.pysquad.cryptobot.subscriber.endpoints.model

import com.fasterxml.jackson.annotation.JsonInclude
import me.pysquad.cryptobot.subscriber.model.ProductIds
import org.http4k.core.Body
import org.http4k.format.Jackson.auto

@JsonInclude(JsonInclude.Include.NON_EMPTY)
data class SubscriberResponse(
        var type: String = "",
        var message: String = "",
        var reason: String = "",
        var data: ProductIds = emptyList()
) {
    companion object {
        val lens = Body.auto<SubscriberResponse>().toLens()

        fun success(type: String, message: String, data: ProductIds) =
                SubscriberResponse(type = type, message = message, data = data)

        fun failure(type: String, message: String, reason: String) =
                SubscriberResponse(type = type, message = message, reason = reason)
    }
}