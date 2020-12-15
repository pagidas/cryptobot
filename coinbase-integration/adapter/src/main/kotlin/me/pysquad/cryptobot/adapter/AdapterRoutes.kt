package me.pysquad.cryptobot.adapter

import me.pysquad.cryptobot.adapter.coinbaseprofiles.endpoints.getProfiles
import me.pysquad.cryptobot.adapter.coinbaseprofiles.service.ProfilesService

fun adapterRoutes(service: ProfilesService) = mutableListOf(
        getProfiles(service)
)