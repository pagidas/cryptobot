package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
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
    CoinbaseWsMessagesRepo(Database(ConfigReader.db)))