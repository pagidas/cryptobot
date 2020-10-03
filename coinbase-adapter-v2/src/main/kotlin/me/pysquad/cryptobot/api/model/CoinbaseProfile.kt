package me.pysquad.cryptobot.api.model

typealias CoinbaseProfiles = List<CoinbaseProfile>

data class CoinbaseProfile(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: String
)