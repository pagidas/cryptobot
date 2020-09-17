package me.pysquad.cryptobot

import io.micronaut.runtime.Micronaut

object GraphQLApplication {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("me.pysquad.cryptobot")
                .mainClass(GraphQLApplication.javaClass)
                .start()
    }
}