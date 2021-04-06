package me.pysquad.cryptobot.subscriber

import me.pysquad.cryptobot.http4k.starter.RoutingHttpHandlers
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

class SubscriberWebController(private val subscriberService: SubscriberService) {

    fun routes(): RoutingHttpHandlers = listOf(
        subscribe(subscriberService),
        createCoinbaseProductSubscription(subscriberService)
    )

    // TODO: 06/04/2021 Should be removed once the new endpoint is merged and used from the client.
    private fun subscribe(service: SubscriberService): RoutingHttpHandler =
        "/subscribe" bind Method.POST to { req: Request ->

            with(Body.auto<ProductIds>().toLens()) {
                val response = service.createCoinbaseProductSubscription(extract(req))

                if (response.isRight)
                    Response(Status.CREATED)
                        .with(CoinbaseProductSubscriptionSuccess.lens of response.get())
                else
                    Response(Status.UNPROCESSABLE_ENTITY)
                        .with(CoinbaseProductSubscriptionFailure.lens of response.left)
            }
        }

    private fun createCoinbaseProductSubscription(service: SubscriberService): RoutingHttpHandler =
        "/coinbase-product-subscriptions" bind Method.POST to { req: Request ->

            with(Body.auto<ProductIds>().toLens()) {
                val response = service.createCoinbaseProductSubscription(extract(req))

                if (response.isRight)
                    Response(Status.CREATED)
                        .with(CoinbaseProductSubscriptionSuccess.lens of response.get())
                else
                    Response(Status.UNPROCESSABLE_ENTITY)
                        .with(CoinbaseProductSubscriptionFailure.lens of response.left)
            }
        }
}