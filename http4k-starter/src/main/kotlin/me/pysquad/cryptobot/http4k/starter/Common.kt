package me.pysquad.cryptobot.http4k.starter

import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.contract.contract
import org.http4k.core.*
import org.http4k.filter.RequestFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias ContractRoutes = MutableList<ContractRoute>

/*
Common top-level function to bootstrap a http4k app.
Used in all http4k apps, published in local maven repo.
 */
fun http4kApp(port: Int = 8080, buildRoutes: () -> ContractRoutes): Http4kApp {
    return object: Http4kApp {
        override val port: Int = port
        override val endpoints: ContractRoutes = buildRoutes()
    }
}

interface Http4kApp {
    private val logger: Logger
        get() = LoggerFactory.getLogger(Http4kApp::class.java)
    val port: Int
    val endpoints: ContractRoutes

    fun run(): Http4kServer {
        // declares basic health route to audit if server is up and running.
        val health: ContractRoute =
                "/health" bindContract Method.GET to {
                    val lens = Body.auto<Map<String, String>>().toLens()
                    Response(Status.OK).with(lens of mapOf("status" to "UP"))
                }

        // builds the contract
        val contract = contract {
            routes += health
            routes += endpoints
        }

        // builds tapping request filter
        val tapReqFilter = RequestFilters.Tap { req ->
            if (req.uri.path.contentEquals("/health"))
                logger.trace("${req.method} ${req.uri}")
            else
                logger.debug("${req.method} ${req.uri}")
        }

        // applies the filter to routes
        val routes = tapReqFilter.then(contract)

        return routes.asServer(Jetty(port)).start().apply {
            logger.info("Startup complete. Http4k app running[:$port]")
        }
    }
}
