package me.pysquad.cryptobot.api

import me.pysquad.cryptobot.api.CryptobotJson.auto
import me.pysquad.cryptobot.api.coinbase.ProductId
import org.http4k.core.Body

data class CoinbaseSubscribeRequest(
    val type: CoinbaseMessageType,
    val productIds: List<ProductId>,
    val channels: List<Channel>
) {
    companion object {
        val lens = Body.auto<CoinbaseSubscribeRequest>().toLens()
    }
}
