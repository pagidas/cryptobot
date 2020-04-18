package me.pysquad.cryptobot.api

enum class CoinbaseMessageType { Subscribe, Unsubscribe, Subscriptions }
data class Channel(val value: String)
