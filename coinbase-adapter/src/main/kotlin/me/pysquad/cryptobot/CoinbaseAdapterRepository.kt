package me.pysquad.cryptobot

import com.rethinkdb.gen.exc.ReqlOpFailedError
import me.pysquad.cryptobot.coinbase.CoinbaseProductMessage
import org.http4k.core.Credentials
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

interface CoinbaseAdapterRepository {
    fun store(messages: List<CoinbaseProductMessage>)
    fun getCoinbaseSandboxApiCredentials(): CoinbaseSandboxApiCredentials
    fun getCoinbaseAdapterAuthCredentials(): Credentials
}

class CoinbaseAdapterRepoImpl(realTimeDb: RealTimeDb): CoinbaseAdapterRepository {
    private val r = realTimeDb.rethinkCtx
    private val conn = realTimeDb.connection

    override fun store(messages: List<CoinbaseProductMessage>) =
        messages.forEach {
            r.table(MESSAGES).insert(it.toDbHashMap()).runNoReply(conn)
        }

    override fun getCoinbaseSandboxApiCredentials(): CoinbaseSandboxApiCredentials =
        r.table(API_KEYS)
            .filter { it.g("api_id").eq("cb_sandbox_api_creds") }
            .pluck("coinbase_sandbox_api_key", "coinbase_sandbox_api_secret", "coinbase_sandbox_api_passphrase")
            .run(conn, HashMap::class.java).next()

                ?.let {
                    CoinbaseSandboxApiCredentials(
                            key = it["coinbase_sandbox_api_key"] as String,
                            secret = it["coinbase_sandbox_api_secret"] as String,
                            passphrase = it["coinbase_sandbox_api_passphrase"] as String
                    )
                } ?: throw ReqlOpFailedError("coinbase api credentials not found.")

    override fun getCoinbaseAdapterAuthCredentials(): Credentials =
        r.table(COINBASE_ADAPTER_AUTH)
            .filter { it.g("auth_id").eq("basic_auth") }
            .pluck("username", "password")
            .run(conn, HashMap::class.java).next()

                ?.let {
                    Credentials(
                        user = it["username"] as String,
                        password = it["password"] as String
                    )
                } ?: throw ReqlOpFailedError("coinbase adapter auth credentials not found.")

    private fun CoinbaseProductMessage.toDbHashMap() =
        hashMapOf(
            "type" to type.name,
            "sequence" to sequence.value,
            "product_id" to product_id.value,
            "price" to price.value,
            "open_24h" to open_24h.value,
            "volume_24h" to volume_24h.value,
            "low_24h" to low_24h.value,
            "high_24h" to high_24h.value,
            "volume_30d" to volume_30d.value,
            "best_bid" to best_bid.value,
            "best_ask" to best_ask.value,
            "side" to side.name,
            "time" to time.toOffsetDateTimeUTC(),
            "trade_id" to trade_id.value,
            "last_size" to last_size.value
        )
}

private fun Instant.toOffsetDateTimeUTC() =
    OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
