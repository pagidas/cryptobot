package me.pysquad.cryptobot.api

import com.rethinkdb.gen.ast.Db
import me.pysquad.cryptobot.RealTimeDb
import me.pysquad.cryptobot.api.coinbase.CoinbaseProductMessage
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

private const val CRYPTOBOT = "cryptobot"
private const val MESSAGES = "messages"

interface CoinbaseWsMessagesRepository {
    fun store(messages: List<CoinbaseProductMessage>)
}

class CoinbaseWsMessagesRepo(realTimeDb: RealTimeDb): CoinbaseWsMessagesRepository {
    private val r = realTimeDb.rethinkWrapper
    private val conn = realTimeDb.connection
    private val cryptoDb = r.db(CRYPTOBOT)

    private fun inTransaction(db: Db = cryptoDb, fn: Db.() -> Unit = {}) = db.fn()

    override fun store(messages: List<CoinbaseProductMessage>) {
        inTransaction {
            messages.forEach {
                table(MESSAGES).insert(it.toDbHashMap()).run(conn)
            }
        }
    }

    private fun CoinbaseProductMessage.toDbHashMap() =
        r.hashMap("type", type.name)
            .with("sequence", sequence.value)
            .with("product_id", product_id.value)
            .with("price", price.value)
            .with("open_24h", open_24h.value)
            .with("volume_24h", volume_24h.value)
            .with("low_24h", low_24h.value)
            .with("high_24h", high_24h.value)
            .with("volume_30d", volume_30d.value)
            .with("best_bid", best_bid.value)
            .with("best_ask", best_ask.value)
            .with("side", side.name)
            .with("time", time.toOffsetDateTimeUTC())
            .with("trade_id", trade_id.value)
            .with("last_size", last_size.value)
}

private fun Instant.toOffsetDateTimeUTC() =
    OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
