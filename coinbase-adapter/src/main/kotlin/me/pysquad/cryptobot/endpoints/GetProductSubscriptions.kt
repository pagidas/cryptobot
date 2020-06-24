package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.CoinbaseAdapterJson.auto
import me.pysquad.cryptobot.CoinbaseAdapterService
import me.pysquad.cryptobot.coinbase.ProductId
import me.pysquad.cryptobot.coinbase.ProductsIds
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with

private val lens = Body.auto<ProductsIds>().toLens()

class GetProductSubscriptions(private val coinbaseAdapter: CoinbaseAdapterService): Endpoint {
    private val exampleGetProductSubscriptionsResponse =
            listOf(ProductId("BTC-EUR"), ProductId("ETH-GBP"))

    override val contractRoute: ContractRoute =
            "/subscriptions" meta {
                summary = "Lists the products we are subscribed to in the coinbase wsfeed market."
                returning(OK, lens to exampleGetProductSubscriptionsResponse)
                security = SecurityProvider.basicAuth()
            } bindContract GET to handler()

    private fun handler(): HttpHandler = { _: Request ->
        Response(OK).with(lens of coinbaseAdapter.getProductSubscriptions())
    }
}