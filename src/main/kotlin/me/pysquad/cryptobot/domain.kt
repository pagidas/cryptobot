package me.pysquad.cryptobot

enum class CoinbaseMessageType { SUBSCRIBE, UNSUBSCRIBE, SUBSCRIPTIONS }
data class ProductId(val value: String)
data class Channel(val value: String)