package me.pysquad.cryptobot.adapter

import me.pysquad.cryptobot.adapter.coinbaseprofiles.endpoints.getProfiles
import me.pysquad.cryptobot.adapter.coinbaseprofiles.service.ProfilesService
import me.pysquad.cryptobot.http4k.starter.RoutingHttpHandlers

fun adapterRoutes(service: ProfilesService): RoutingHttpHandlers = listOf(
        getProfiles(service)
)