package me.pysquad.cryptobot.coinbaseprofiles.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import me.pysquad.cryptobot.api.CoinbaseApi
import me.pysquad.cryptobot.api.model.CoinbaseProfiles

@Controller("/profiles")
class ProfilesController(private val coinbase: CoinbaseApi) {

    @Get("/", produces = [ MediaType.APPLICATION_JSON ])
    fun getCoinbaseProfiles(): HttpResponse<CoinbaseProfiles> =
            coinbase.getProfiles().body()?.let {
                if (it.isNotEmpty())
                    HttpResponse.ok(it)
                else
                    HttpResponse.ok(emptyList())
            } ?: HttpResponse.ok(emptyList())
}