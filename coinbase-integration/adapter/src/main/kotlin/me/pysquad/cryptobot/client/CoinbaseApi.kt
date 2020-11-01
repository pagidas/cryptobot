package me.pysquad.cryptobot.client

import me.pysquad.cryptobot.client.model.CoinbaseProfiles
import org.http4k.client.OkHttp
import org.http4k.core.*
import org.http4k.filter.ClientFilters
import org.http4k.format.Jackson.auto
import org.slf4j.LoggerFactory

interface CoinbaseApi {
    val baseUri: Uri
    val httpClient: HttpHandler

    fun getProfiles(): CoinbaseProfiles

    companion object {
        fun client(coinbaseConfig: CoinbaseConfiguration) = object : CoinbaseApi {
            private val logger = LoggerFactory.getLogger(CoinbaseApi::class.java)

            override val baseUri: Uri = coinbaseConfig.uri
            override val httpClient: HttpHandler =
                    ClientFilters.SetBaseUriFrom(baseUri).then(CoinbaseAuthFilter(coinbaseConfig)).then(OkHttp())

            override fun getProfiles(): CoinbaseProfiles {
                val lens = Body.auto<CoinbaseProfiles>().toLens()
                val response = httpClient(Request(Method.GET, "/profiles"))
                logger.debug("Got response from coinbase api: {}, {}", response.status, response.bodyString())
                return lens(response)
            }
        }
    }
}
