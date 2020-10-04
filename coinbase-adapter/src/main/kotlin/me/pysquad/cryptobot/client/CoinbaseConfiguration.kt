package me.pysquad.cryptobot.client

import io.micronaut.context.annotation.ConfigurationProperties
import javax.validation.constraints.NotBlank

@ConfigurationProperties("coinbase")
class CoinbaseConfiguration {

    @NotBlank
    lateinit var url: String
    var authentication = AuthenticationConfiguration()

    @ConfigurationProperties("authentication")
    class AuthenticationConfiguration {
        @NotBlank
        lateinit var key: String
        @NotBlank
        lateinit var secret: String
        @NotBlank
        lateinit var passphrase: String
    }
}