package me.pysquad.cryptobot.common

import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface Http4kApp {
    private val logger: Logger
        get() = LoggerFactory.getLogger(Http4kApp::class.java)
    val port: Int
    val routes: RoutingHttpHandler
    fun run(): Http4kServer = with(routes) {
        asServer(Jetty(port)).start().apply {
            logger.info("Startup complete. Server running...")
        }
    }
}