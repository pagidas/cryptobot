package me.pysquad.cryptobot.adapter.client.model

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

typealias CoinbaseProfiles = List<CoinbaseProfile>

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class CoinbaseProfile(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: String
)