package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi

class CryptobotServer(val coinbase: CoinbaseApi) {
    companion object {
        fun build() =
            CryptobotServer(
                coinbase = CoinbaseApi.Client()
            )
    }
}