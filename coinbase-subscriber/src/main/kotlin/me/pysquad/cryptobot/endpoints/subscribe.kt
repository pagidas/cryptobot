package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.endpoints.model.SubscriberResponse
import me.pysquad.cryptobot.model.ProductIds
import me.pysquad.cryptobot.service.CoinbaseSubscriberService
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.core.Body
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.format.Jackson.auto

fun subscribe(service: CoinbaseSubscriberService): ContractRoute =
        "/subscribe" bindContract Method.POST to { req: Request ->

            with(Body.auto<ProductIds>().toLens()) {
                val response = service.subscribe(extract(req))

                if (response.type != "error")
                    Response(Status.CREATED).with(SubscriberResponse.lens of response)
                else
                    Response(Status.FORBIDDEN).with(SubscriberResponse.lens of response)
            }
        }