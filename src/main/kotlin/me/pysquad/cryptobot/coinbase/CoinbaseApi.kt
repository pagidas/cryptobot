package me.pysquad.cryptobot.coinbase

interface CoinbaseApi {
    fun subscribe(): String

    companion object {

        fun Client() = object: CoinbaseApi {
            override fun subscribe() = "Here is subscribe functionality from coinbase!"
        }
    }
}