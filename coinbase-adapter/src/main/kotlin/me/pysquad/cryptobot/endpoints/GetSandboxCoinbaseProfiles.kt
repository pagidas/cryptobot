package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.CoinbaseAdapterService
import me.pysquad.cryptobot.SandboxCoinbaseProfile
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.with
import java.time.Instant

class GetSandboxCoinbaseProfiles(private val coinbaseAdapter: CoinbaseAdapterService): Endpoint {
    private val exampleGetSandboxCoinbaseProfilesResponse =
            listOf(
                SandboxCoinbaseProfile(
                    id = "86602c68-306a-4500-ac73-4ce56a91d83c",
                    userId = "5844eceecf7e803e259d0365",
                    name = "default",
                    active = true,
                    isDefault = true,
                    createdAt = Instant.parse("2019-11-18T15:08:40.236309Z" as CharSequence)
                ),
                SandboxCoinbaseProfile(
                    id = "82212n21-376b-1234-bn43-1er64g12h43w",
                    userId = "5844eceecf7e803e259d0365",
                    name = "default",
                    active = false,
                    isDefault = false,
                    createdAt = Instant.parse("2019-11-18T15:08:40.236309Z" as CharSequence)
                )
            )

    override val contractRoute: ContractRoute =
        "/coinbase/sandbox/profiles" meta {
            summary = "Lists the profiles of that coinbase account."
            returning(OK, SandboxCoinbaseProfile.listLens to exampleGetSandboxCoinbaseProfilesResponse)
            security = SecurityProvider.basicAuth()
        } bindContract GET to handler()

    private fun handler(): HttpHandler = { _: Request ->
        Response(OK).with(SandboxCoinbaseProfile.listLens of coinbaseAdapter.getSandboxCoinbaseProfiles())
    }
}