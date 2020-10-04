package me.pysquad.cryptobot.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

typealias CoinbaseProfiles = List<CoinbaseProfile>

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class CoinbaseProfile(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: String
)