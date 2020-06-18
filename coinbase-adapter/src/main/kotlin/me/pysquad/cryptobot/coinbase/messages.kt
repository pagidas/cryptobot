package me.pysquad.cryptobot.coinbase

import org.http4k.core.Body
import org.http4k.core.HttpMessage
import org.http4k.format.Jackson.json
import org.http4k.websocket.WsMessage
import java.time.Instant

data class CoinbaseProductMessage(
        val type: Type,
        val sequence: CoinSequence,
        val productId: ProductId,
        val price: Price,
        val open24h: Open24h,
        val volume24h: Volume24h,
        val low24h: Low24h,
        val high24h: High24h,
        val volume30d: Volume30d,
        val bestBid: BestBid,
        val bestAsk: BestAsk,
        val side: Side,
        val time: Instant,
        val tradeId: TradeId,
        val lastSize: LastSize
) {
    companion object {
        fun fromWsMessage(wsMessage: WsMessage): CoinbaseProductMessage {
            val wsJsonLens = WsMessage.json().toLens()

            return with(wsJsonLens(wsMessage)) {
                CoinbaseProductMessage(
                        Type.betterValueOf(get("type").asText()),
                        CoinSequence(get("sequence").asLong()),
                        ProductId(get("product_id").asText()),
                        Price(get("price").asText()),
                        Open24h(get("open_24h").asText()),
                        Volume24h(get("volume_24h").asText()),
                        Low24h(get("low_24h").asText()),
                        High24h(get("high_24h").asText()),
                        Volume30d(get("volume_30d").asText()),
                        BestBid(get("best_bid").asText()),
                        BestAsk(get("best_ask").asText()),
                        Side.betterValueOf(get("side").asText()),
                        Instant.parse(get("time").asText() as CharSequence),
                        TradeId(get("trade_id").asLong()),
                        LastSize(get("last_size").asText())
                )
            }
        }
    }
}

data class GetSandboxCoinbaseProfileMessage(
        val id: String,
        val userId: String,
        val name: String,
        val active: Boolean,
        val isDefault: Boolean,
        val createdAt: String
) {
    companion object {
        fun toListOfMessages(httpMessage: HttpMessage): List<GetSandboxCoinbaseProfileMessage> {
            val httpJsonLens = Body.json().toLens()

            return with(httpJsonLens(httpMessage)) {
                map {
                    GetSandboxCoinbaseProfileMessage(
                        it["id"].asText(),
                        it["user_id"].asText(),
                        it["name"].asText(),
                        it["active"].asBoolean(),
                        it["is_default"].asBoolean(),
                        it["created_at"].asText()
                    )
                }
            }
        }
    }
}
