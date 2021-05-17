package me.pysquad.cryptobot.http4k.starter

import org.http4k.core.*
import org.http4k.filter.RequestFilters
import org.http4k.filter.ServerFilters
import org.http4k.format.Jackson.auto
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

typealias RoutingHttpHandlers = List<RoutingHttpHandler>

/*
Common top-level function to bootstrap a http4k app.
Used in all http4k apps, published in local maven repo.
 */
fun http4kApp(port: Int = 8080, buildRoutes: () -> RoutingHttpHandlers): Http4kApp {
    return object: Http4kApp {
        override val log = LoggerFactory.getLogger(Http4kApp::class.java)
        override val port: Int = port
        override val endpoints: RoutingHttpHandlers = buildRoutes()
    }
}

interface Http4kApp {
    val log: Logger
    val port: Int
    val endpoints: RoutingHttpHandlers

    fun run(): Http4kServer {
        // declares basic health route to audit if server is up and running.
        val health: RoutingHttpHandler =
            "/health" bind Method.GET to {
                val lens = Body.auto<Map<String, String>>().toLens()
                Response(Status.OK).with(lens of mapOf("status" to "UP"))
            }

        val contract = routes(health, *endpoints.toTypedArray())

        // builds tapping request filter
        val tapReqFilter = RequestFilters.Tap { req ->
            if (req.uri.path.contentEquals("/health"))
                log.trace("${req.method} ${req.uri}")
            else
                log.debug("${req.method} ${req.uri}")
        }

        // applies the filters to routes
        val routes = ServerFilters.CatchAll().then(tapReqFilter).then(contract)

        return routes.asServer(Jetty(port)).start().apply {
            log.info("Startup complete. Http4k app running[:$port]")
        }
    }
}
