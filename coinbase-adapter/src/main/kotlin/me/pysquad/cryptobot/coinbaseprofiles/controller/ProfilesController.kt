package me.pysquad.cryptobot.coinbaseprofiles.controller

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import me.pysquad.cryptobot.client.model.CoinbaseProfiles
import me.pysquad.cryptobot.coinbaseprofiles.service.ProfilesService

@Controller("/profiles")
class ProfilesController(private val service: ProfilesService) {

    @Get(produces = [ MediaType.APPLICATION_JSON ])
    fun getCoinbaseProfiles(): HttpResponse<CoinbaseProfiles> {
        val profiles = service.getCoinbaseProfiles()
        return HttpResponse.ok(profiles)
    }
}