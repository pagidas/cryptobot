package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.annotation.JsonInclude
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
