package me.pysquad.cryptobot.common

import org.http4k.routing.RoutingHttpHandler
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

interface Http4kApp {
    val port: Int
    val routes: RoutingHttpHandler
    fun run(): Http4kServer = with(routes) {
        asServer(port.let(::Jetty)).start()
    }
}