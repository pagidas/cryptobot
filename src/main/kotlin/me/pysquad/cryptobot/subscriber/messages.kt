package me.pysquad.cryptobot.subscriber

import me.pysquad.cryptobot.Channel
import me.pysquad.cryptobot.CoinbaseMessageType
import me.pysquad.cryptobot.ProductId
import org.http4k.core.Body
import me.pysquad.cryptobot.CryptobotJson.auto

data class CoinbaseSubscribeRequest(
    val type: CoinbaseMessageType,
    val productIds: List<ProductId>,
    val channels: List<Channel>
) {
    companion object {
        val lens = Body.auto<CoinbaseSubscribeRequest>().toLens()
    }
}
