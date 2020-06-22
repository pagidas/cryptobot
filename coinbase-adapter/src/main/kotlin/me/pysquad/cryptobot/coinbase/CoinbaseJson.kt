package me.pysquad.cryptobot.coinbase

import com.fasterxml.jackson.databind.ObjectMapper
import org.http4k.format.AutoMappingConfiguration
import org.http4k.lens.BiDiMapping
import java.time.Instant

fun AutoMappingConfiguration<ObjectMapper>.withCoinbaseMappings() = apply {
    text(BiDiMapping(::typeAsIn, ::typeAsOut))
    text(BiDiMapping(::sideAsIn, ::sideAsOut))
    long(BiDiMapping(::CoinSequence, CoinSequence::value))
    long(BiDiMapping(::TradeId, TradeId::value))
    text(BiDiMapping(::ProductId, ProductId::value))
    text(BiDiMapping(::Price, Price::value))
    text(BiDiMapping(::Open24h, Open24h::value))
    text(BiDiMapping(::Volume24h, Volume24h::value))
    text(BiDiMapping(::Low24h, Low24h::value))
    text(BiDiMapping(::High24h, High24h::value))
    text(BiDiMapping(::Volume30d, Volume30d::value))
    text(BiDiMapping(::BestBid, BestBid::value))
    text(BiDiMapping(::BestAsk, BestAsk::value))
    text(BiDiMapping(::LastSize, LastSize::value))
    BiDiMapping(Instant::parse, Instant::toString)
}

private fun typeAsIn(s: String) = Type.betterValueOf(s)
private fun typeAsOut(enum: Type) = enum.name.toLowerCase()
private fun sideAsIn(s: String) = Side.betterValueOf(s)
private fun sideAsOut(enum: Side) = enum.name.toLowerCase()
