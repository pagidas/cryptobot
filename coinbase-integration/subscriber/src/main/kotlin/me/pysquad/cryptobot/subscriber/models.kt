package me.pysquad.cryptobot.subscriber

import org.http4k.core.Body
import org.http4k.format.Jackson.auto

data class CoinbaseProductSubscriptionSuccess(val productIds: ProductIds) {
    companion object {
        val lens = Body.auto<CoinbaseProductSubscriptionSuccess>().toLens()
    }
}

data class CoinbaseProductSubscriptionFailure(
    val type: String,
    val message: String,
    val reason: String
) {
    companion object {
        val lens = Body.auto<CoinbaseProductSubscriptionFailure>().toLens()
    }
}
