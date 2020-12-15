package me.pysquad.cryptobot.graphql

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
            .packages("me.pysquad.cryptobot.graphql")
            .mainClass(Application.javaClass)
            .start()
    }
}