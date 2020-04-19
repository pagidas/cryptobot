package me.pysquad.cryptobot

import me.pysquad.cryptobot.api.CoinbaseWsMessagesRepo
import me.pysquad.cryptobot.api.CryptobotService
import me.pysquad.cryptobot.api.coinbase.CoinbaseApi
import me.pysquad.cryptobot.config.ConfigReader

class CryptobotServer(val coinbase: CoinbaseApi) {
    companion object {
        fun build() =
            CryptobotServer(
                coinbase = CoinbaseApi.Client(buildCryptobotService())
            )
    }
}

private fun buildCryptobotService() = CryptobotService(
    CoinbaseWsMessagesRepo(RealTimeDb(ConfigReader.db))
)