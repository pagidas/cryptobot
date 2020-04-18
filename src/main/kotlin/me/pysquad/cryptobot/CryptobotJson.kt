package me.pysquad.cryptobot

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.http4k.format.AutoMappingConfiguration
import org.http4k.format.ConfigurableJackson
import org.http4k.format.asConfigurable
import org.http4k.format.withStandardMappings
import org.http4k.lens.BiDiMapping

object CryptobotJson: ConfigurableJackson(KotlinModule()
    .asConfigurable()
    .withStandardMappings()
    .withCryptobotMappings()
    .done()
    .deactivateDefaultTyping()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
    .configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    .configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
)

fun AutoMappingConfiguration<ObjectMapper>.withCryptobotMappings() = apply {
    text(BiDiMapping(::ProductId, ProductId::value))
    text(BiDiMapping(::Channel, Channel::value))
}
