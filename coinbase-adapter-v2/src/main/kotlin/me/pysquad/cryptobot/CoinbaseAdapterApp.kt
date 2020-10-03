package me.pysquad.cryptobot

import io.micronaut.runtime.Micronaut.build

fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("me.pysquad.cryptobot")
		.start()
}

