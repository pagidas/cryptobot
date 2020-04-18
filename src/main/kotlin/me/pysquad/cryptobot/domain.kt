package me.pysquad.cryptobot

enum class CoinbaseMessageType { Subscribe, Unsubscribe, Subscriptions }
data class ProductId(val value: String)
data class Channel(val value: String)