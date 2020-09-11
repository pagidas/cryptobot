package me.pysquad.cryptobot.model

data class CoinbaseMessage(
        val type: String,
        val sequence: String,
        val productId: String,
        val price: String,
        val open24h: String,
        val volume24h: String,
        val low24h: String,
        val high24h: String,
        val volume30d: String,
        val bestBid: String,
        val bestAsk: String,
        val side: String,
        val time: String,
        val tradeId: String,
        val lastSize: String
)