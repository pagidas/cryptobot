package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.CoinbaseAdapterJson.auto
import org.http4k.websocket.WsMessage
import java.time.Instant

data class CoinbaseProductMessage(
    val type: Type,
    val sequence: CoinSequence,
    val product_id: ProductId,
    val price: Price,
    val open_24h: Open24h,
    val volume_24h: Volume24h,
    val low_24h: Low24h,
    val high_24h: High24h,
    val volume_30d: Volume30d,
    val best_bid: BestBid,
    val best_ask: BestAsk,
    val side: Side,
    val time: Instant,
    val trade_id: TradeId,
    val last_size: LastSize
) {
    companion object {
        val wsLens = WsMessage.auto<CoinbaseProductMessage>().toLens()
    }
}

data class GetSandboxCoinbaseMessage(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: String
)