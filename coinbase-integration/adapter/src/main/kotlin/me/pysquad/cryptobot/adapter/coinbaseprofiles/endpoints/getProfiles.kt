package me.pysquad.cryptobot.adapter.coinbaseprofiles.endpoints

import me.pysquad.cryptobot.adapter.client.model.CoinbaseProfiles
import me.pysquad.cryptobot.adapter.coinbaseprofiles.service.ProfilesService
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