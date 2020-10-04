package me.pysquad.cryptobot.client

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Header
import io.micronaut.http.client.annotation.Client
import me.pysquad.cryptobot.client.model.CoinbaseProfiles

@Client("\${coinbase.url}")
@Header(name = "User-Agent", value = "coinbase-adapter")
interface CoinbaseApi {

    @Get("/profiles", produces = [ MediaType.APPLICATION_JSON ])
    fun getProfiles(): HttpResponse<CoinbaseProfiles>
}