package me.pysquad.cryptobot.coinbaseprofiles.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import me.pysquad.cryptobot.api.CoinbaseConfiguration

@Controller("/profiles")
class ProfilesController(private val config: CoinbaseConfiguration) {

    @Get("/app-config", produces = [MediaType.APPLICATION_JSON])
    fun getProfiles(): HttpResponse<CoinbaseConfiguration>? {
        return HttpResponse.ok(config)
    }
}