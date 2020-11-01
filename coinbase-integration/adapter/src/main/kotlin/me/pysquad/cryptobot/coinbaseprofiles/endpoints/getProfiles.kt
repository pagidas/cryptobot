package me.pysquad.cryptobot.coinbaseprofiles.endpoints

import me.pysquad.cryptobot.client.model.CoinbaseProfiles
import me.pysquad.cryptobot.coinbaseprofiles.service.ProfilesService
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.core.*
import org.http4k.format.Jackson.auto

fun getProfiles(service: ProfilesService): ContractRoute =
        "/profiles" bindContract Method.GET to { _: Request ->
            val lens = Body.auto<CoinbaseProfiles>().toLens()
            val profiles = service.getCoinbaseProfiles()
            Response(Status.OK).with(lens of profiles)
        }