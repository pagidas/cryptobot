package me.pysquad.cryptobot.subscriber

import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.core.*
import org.http4k.format.Jackson.auto

class SubscriberWebController(private val subscriberService: SubscriberService) {

    fun routes() = mutableListOf(
        subscribe(subscriberService),
        createCoinbaseProductSubscription(subscriberService)
    )

    /**
     * Should be removed once the new endpoint is merged and used from the client.
     */
    private fun subscribe(service: SubscriberService): ContractRoute =
        "/subscribe" bindContract Method.POST to { req: Request ->

            with(Body.auto<ProductIds>().toLens()) {
                val response = service.subscribe(extract(req))

                if (response.type != "error")
                    Response(Status.CREATED).with(SubscriberResponse.lens of response)
                else
                    Response(Status.FORBIDDEN).with(SubscriberResponse.lens of response)
            }
        }

    private fun createCoinbaseProductSubscription(service: SubscriberService): ContractRoute =
        "/coinbase-product-subscriptions" bindContract Method.POST to { req: Request ->

            with(Body.auto<ProductIds>().toLens()) {
                val response = service.subscribe(extract(req))

                if (response.type != "error")
                    Response(Status.CREATED).with(SubscriberResponse.lens of response)
                else
                    Response(Status.FORBIDDEN).with(SubscriberResponse.lens of response)
            }
        }
}