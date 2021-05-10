package me.pysquad.cryptobot.subscriber

import me.pysquad.cryptobot.http4k.starter.RoutingHttpHandlers
import org.http4k.core.*
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

class SubscriberWebController(private val service: SubscriberService) {

    fun routes(): RoutingHttpHandlers = listOf(
        testWsFeedNonBlocking
    )

    private val testWsFeedNonBlocking: RoutingHttpHandler =
        "/coinbase-product-subscriptions" bind Method.POST to { request: Request ->

            with(CoinbaseProductSubscriptionRequest.lens) {
                val productId = extract(request).productId
                val response = service.createCoinbaseProductSubscriptionV2(productId)

                if (response.isRight)
                    Response(Status.ACCEPTED)
                else
                    Response(Status.UNPROCESSABLE_ENTITY).with(CoinbaseProductSubscriptionFailure.lens of response.left)
            }
        }
}