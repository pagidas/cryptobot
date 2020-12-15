package me.pysquad.cryptobot.graphql.rethinkdb

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("rethinkdb")
class RethinkDbConfiguration {
    @NotBlank
    lateinit var port: String
    @NotBlank
    lateinit var host: String
}