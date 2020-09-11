package me.pysquad.cryptobot.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class CoinbaseMessage(
    var type: String = "",
    var sequence: String = "",
    var productId: String = "",
    var price: String = "",
    var open24h: String = "",
    var volume24h: String = "",
    var low24h: String = "",
    var high24h: String = "",
    var volume30d: String = "",
    var bestBid: String = "",
    var bestAsk: String = "",
    var side: String = "",
    var time: String = "",
    var tradeId: String = "",
    var lastSize: String = ""
)