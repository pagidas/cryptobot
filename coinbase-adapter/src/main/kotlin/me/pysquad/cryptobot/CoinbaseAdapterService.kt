package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi

class CoinbaseAdapterService(private val coinbase: CoinbaseApi, private val coinbaseAdapterRepository: CoinbaseAdapterRepository) {

    companion object {
        fun buildIt() = CoinbaseAdapterService(CoinbaseApi.buildIt(), CoinbaseAdapterRepoImpl(RealTimeDb))
    }

    fun getSandboxCoinbaseProfiles() =
            coinbase.getSandboxCoinbaseProfiles()
                    .map { message -> SandboxCoinbaseProfile.of(message) }

    fun getProductSubscriptions() = coinbaseAdapterRepository.getSubscriptions()
}