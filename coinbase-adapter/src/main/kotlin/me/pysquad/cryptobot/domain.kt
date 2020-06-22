package me.pysquad.cryptobot

enum class CoinbaseMessageType {
    SUBSCRIBE,
    UNSUBSCRIBE,
    SUBSCRIPTIONS,
    ERROR;

    companion object {
        fun betterValueOf(s: String) = valueOf(s.toUpperCase())
    }
}
data class Channel(val value: String)
data class CoinbaseSandboxApiCredentials(val key: String, val secret: String, val passphrase: String)
