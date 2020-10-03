package me.pysquad.cryptobot.api

import io.micronaut.http.client.annotation.Client

@Client("\${coinbase.url}")
interface CoinbaseApi {
}