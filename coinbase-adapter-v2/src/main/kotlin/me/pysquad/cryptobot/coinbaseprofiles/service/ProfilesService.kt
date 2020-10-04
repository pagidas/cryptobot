package me.pysquad.cryptobot.coinbaseprofiles.service

import me.pysquad.cryptobot.client.CoinbaseApi
import me.pysquad.cryptobot.client.model.CoinbaseProfiles
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class ProfilesService(private val coinbaseApi: CoinbaseApi) {

    private val log = LoggerFactory.getLogger(ProfilesService::class.java)

    fun getCoinbaseProfiles(): CoinbaseProfiles {
        log.info("Attempt to get coinbase profiles")
        val response = coinbaseApi.getProfiles()
        log.debug("Got response from coinbase api: Status -- {}, Body -- {}", response.status, response.body())
        return response.body() ?: emptyList()
    }
}