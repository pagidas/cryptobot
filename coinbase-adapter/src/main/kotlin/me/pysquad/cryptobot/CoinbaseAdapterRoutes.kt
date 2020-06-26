package me.pysquad.cryptobot

import me.pysquad.cryptobot.coinbase.CoinbaseApi
import me.pysquad.cryptobot.endpoints.Auth
import me.pysquad.cryptobot.endpoints.GetProductSubscriptions
import me.pysquad.cryptobot.endpoints.GetSandboxCoinbaseProfiles
import me.pysquad.cryptobot.endpoints.SubscribeToMarket
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.contract
import org.http4k.contract.openapi.ApiInfo
import org.http4k.contract.openapi.v3.OpenApi3
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.then
import org.http4k.filter.DebuggingFilters
import org.http4k.filter.ServerFilters
import org.http4k.routing.ResourceLoader
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.routing.static
import java.util.Properties

private val swaggerUiVersion = with(Properties()) {
    CoinbaseAdapterRoutes::class.java.getResourceAsStream(
            "/META-INF/maven/org.webjars/swagger-ui/pom.properties"
    ).use { load(it) }
    getProperty("version")
}

object CoinbaseAdapterRoutes {
    operator fun invoke(coinbaseAdapter: CoinbaseAdapterService, coinbase: CoinbaseApi, securityProvider: SecurityProvider): RoutingHttpHandler {

        val coinbaseAdapterContract = contract {
            renderer = OpenApi3(
                    ApiInfo("coinbase-adapter API", "v1.0", "The API of the adapter service that talks to coinbase"),
                    CoinbaseAdapterJson
            )
            descriptionPath = "/coinbase-adapter"
            routes += listOf(
                    // --- Adapter ---
                    GetProductSubscriptions(coinbaseAdapter),

                    // --- Coinbase ---
                    SubscribeToMarket(coinbase),
                    GetSandboxCoinbaseProfiles(coinbaseAdapter),

                    // --- Auth ---
                    Auth(securityProvider)

            ).map { it.contractRoute }
        }

        return ServerFilters.CatchLensFailure
            .then(DebuggingFilters.PrintRequestAndResponse())
            .then(routes(
                "/api" bind coinbaseAdapterContract,
                "/docs" bind swaggerUi
            ))
    }
}

private val swaggerUi: RoutingHttpHandler = routes(
        "/coinbase-adapter" bind GET to {
            Response(Status.FOUND).header("Location", "/docs/coinbase-adapter/index.html?url=/api/coinbase-adapter")
        },
        "/coinbase-adapter" bind static(ResourceLoader.Classpath("META-INF/resources/webjars/swagger-ui/$swaggerUiVersion"))
)
