package me.pysquad.cryptobot.coinbase

import me.pysquad.cryptobot.CoinbaseAdapterJson.auto
import me.pysquad.cryptobot.CoinbaseMessageType
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
    data class GQLMessage(
            val type: String,
            val sequence: String,
            val productId: String,
            val price: String,
            val open24h: String,
            val volume24h: String,
            val low24h: String,
            val high24h: String,
            val volume30d: String,
            val bestBid: String,
            val bestAsk: String,
            val side: String,
            val time: String,
            val tradeId: String,
            val lastSize: String
    )

    companion object {
        fun fromWsMessage(wsMessage: WsMessage): CoinbaseProductMessage {
            val wsJsonLens = WsMessage.json().toLens()

            return with(wsJsonLens(wsMessage)) {
                CoinbaseProductMessage(
                        Type.betterValueOf(get("type").asText()),
                        CoinSequence(get("sequence").asText()),
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
                        TradeId(get("trade_id").asText()),
                        LastSize(get("last_size").asText())
                )
            }
        }

        fun toGQLMessage(map: HashMap<*, *>): GQLMessage =
            GQLMessage(
                    map["type"] as String,
                    map["sequence"] as String,
                    map["product_id"] as String,
                    map["price"] as String,
                    map["open_24h"] as String,
                    map["volume_24h"] as String,
                    map["low_24h"] as String,
                    map["high_24h"] as String,
                    map["volume_30d"] as String,
                    map["best_bid"] as String,
                    map["best_ask"] as String,
                    map["side"] as String,
                    map["time"].toString(),
                    map["trade_id"] as String,
                    map["last_size"] as String
            )
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

abstract class CoinbaseWsFeedResponse {
    companion object {
        private val wsJsonLens = WsMessage.json().toLens()

        val successLens = Body.auto<CoinbaseWsFeedSuccess>().toLens()
        val errorLens = Body.auto<CoinbaseWsFeedError>().toLens()

        fun success(wsMessage: WsMessage): CoinbaseWsFeedSuccess =
            with(wsJsonLens(wsMessage)) {
                CoinbaseWsFeedSuccess(
                        type = CoinbaseMessageType.betterValueOf(get("type").asText()),
                        message = "Successfully subscribed to coinbase websocket feed",
                        productIds = get("channels")
                                .asIterable().iterator().next()["product_ids"]
                                .asIterable().map { it.asText() }
                )
            }

        fun error(wsMessage: WsMessage): CoinbaseWsFeedError =
            with(wsJsonLens(wsMessage)) {
                CoinbaseWsFeedError(
                        type = CoinbaseMessageType.betterValueOf(get("type").asText()),
                        message = get("message").asText(),
                        reason = get("reason").asText()
                )
            }
    }
}

data class CoinbaseWsFeedSuccess(
        val type: CoinbaseMessageType,
        val message: String,
        val productIds: List<String>
): CoinbaseWsFeedResponse()

data class CoinbaseWsFeedError(
        val type: CoinbaseMessageType,
        val message: String,
        val reason: String
): CoinbaseWsFeedResponse()