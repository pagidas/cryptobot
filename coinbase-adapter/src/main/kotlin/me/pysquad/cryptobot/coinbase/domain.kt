package me.pysquad.cryptobot.coinbase


enum class Type {
    TICKER;

    companion object {
        fun betterValueOf(s: String) = valueOf(s.toUpperCase())
    }
}
enum class Side {
    BUY,
    SELL;

    companion object {
        fun betterValueOf(s: String) = valueOf(s.toUpperCase())
    }
}

data class CoinSequence(val value: Long)
data class ProductId(val value: String)
data class Price(val value: String)
data class Open24h(val value: String)
data class Volume24h(val value: String)
data class Low24h(val value: String)
data class High24h(val value: String)
data class Volume30d(val value: String)
data class BestBid(val value: String)
data class BestAsk(val value: String)
data class TradeId(val value: Long)
data class LastSize(val value: String)
