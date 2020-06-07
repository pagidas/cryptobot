package me.pysquad.cryptobot

enum class CoinbaseMessageType { Subscribe, Unsubscribe, Subscriptions }
data class Channel(val value: String)
data class CoinbaseSandboxApiCredentials(val key: String, val secret: String, val passphrase: String)
