package me.pysquad.cryptobot

import org.http4k.contract.ContractRoute
import org.http4k.contract.contract
import org.http4k.core.then
import org.http4k.filter.RequestFilters
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias ContractRoutes = MutableList<ContractRoute>

/*
Common top-level function to create an http4k app.
Used in all http4k apps coming from a separated common
gradle project, published in local maven.
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
        // builds the contract
        val contract = contract { routes += endpoints }

        // builds tapping request filter
        val tapReqFilter = RequestFilters.Tap { req ->
            logger.debug("${req.method} ${req.uri}")
        }

        // applies the filter to routes
        val routes = tapReqFilter.then(contract)

        return routes.asServer(Jetty(port)).start().apply {
            logger.info("Startup complete. Server running[:$port]")
        }
    }
}