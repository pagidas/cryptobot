package me.pysquad.cryptobot.adapter.coinbaseprofiles.service

import me.pysquad.cryptobot.adapter.client.CoinbaseApi
import me.pysquad.cryptobot.adapter.client.model.CoinbaseProfiles
import org.slf4j.LoggerFactory

class ProfilesService(private val coinbaseApi: CoinbaseApi) {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun getCoinbaseProfiles(): CoinbaseProfiles {
        log.info("Attempt to get coinbase profiles associated with us")
        return coinbaseApi.getProfiles()
    }
}