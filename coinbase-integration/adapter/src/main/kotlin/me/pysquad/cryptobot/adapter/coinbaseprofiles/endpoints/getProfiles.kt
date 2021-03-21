package me.pysquad.cryptobot.adapter.coinbaseprofiles.endpoints

import me.pysquad.cryptobot.adapter.client.model.CoinbaseProfiles
import me.pysquad.cryptobot.adapter.coinbaseprofiles.service.ProfilesService
import org.http4k.core.*
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind

fun getProfiles(service: ProfilesService): RoutingHttpHandler =
        "/profiles" bind Method.GET to { _: Request ->
            val lens = Body.auto<CoinbaseProfiles>().toLens()
            val profiles = service.getCoinbaseProfiles()
            Response(Status.OK).with(lens of profiles)
        }