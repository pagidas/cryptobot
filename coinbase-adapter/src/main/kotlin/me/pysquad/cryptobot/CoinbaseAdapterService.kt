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

    fun getMessagesGQL(limit: Int?, mostRecent: Int?) =
            when {
                limit != null -> coinbaseAdapterRepository.getMessagesByLimit(limit)
                mostRecent != null -> coinbaseAdapterRepository.getMostRecentMessages(mostRecent)
                else -> coinbaseAdapterRepository.getMessages()
            }
}