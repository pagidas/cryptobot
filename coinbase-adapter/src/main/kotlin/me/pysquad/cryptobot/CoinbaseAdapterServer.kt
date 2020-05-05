package me.pysquad.cryptobot

import me.pysquad.cryptobot.api.CoinbaseWsMessagesRepo
import me.pysquad.cryptobot.api.CoinbaseAdapterService
import me.pysquad.cryptobot.api.coinbase.CoinbaseApi
import me.pysquad.cryptobot.config.ConfigReader

class CoinbaseAdapterServer(val coinbase: CoinbaseApi) {
    companion object {
        fun build() =
            CoinbaseAdapterServer(
                coinbase = CoinbaseApi.Client(buildCoinbaseAdapterService())
            )
    }
}

private fun buildCoinbaseAdapterService(): CoinbaseAdapterService {
    RealTimeDb.connect(ConfigReader.db).also { it.runMigrations() }
    return CoinbaseAdapterService(
        CoinbaseWsMessagesRepo(RealTimeDb)
    )
}