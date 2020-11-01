package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbaseprofiles.endpoints.getProfiles
import me.pysquad.cryptobot.coinbaseprofiles.service.ProfilesService

fun adapterRoutes(service: ProfilesService) = mutableListOf(
        getProfiles(service)
)