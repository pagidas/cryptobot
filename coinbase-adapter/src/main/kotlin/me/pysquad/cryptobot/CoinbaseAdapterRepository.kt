package me.pysquad.cryptobot

import com.rethinkdb.gen.exc.ReqlOpFailedError
import me.pysquad.cryptobot.coinbase.CoinbaseProductMessage
import me.pysquad.cryptobot.coinbase.ProductId
import me.pysquad.cryptobot.coinbase.ProductsIds
import org.http4k.core.Credentials
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

interface CoinbaseAdapterRepository {
    fun storeMessages(messages: List<CoinbaseProductMessage>)
    fun getMessages(limit: Int?): List<CoinbaseProductMessage.GQLMessage>
    fun storeSubscriptions(givenSubscriptions: ProductsIds)
    fun getSubscriptions(): ProductsIds
    fun getCoinbaseSandboxApiCredentials(): CoinbaseSandboxApiCredentials
    fun getCoinbaseAdapterAuthCredentials(): Credentials
}

class CoinbaseAdapterRepoImpl(realTimeDb: RealTimeDb): CoinbaseAdapterRepository {
    private val r = realTimeDb.rethinkCtx
    private val conn = realTimeDb.connection

    override fun storeMessages(messages: List<CoinbaseProductMessage>) =
        messages.forEach {
            r.table(MESSAGES).insert(it.toDbHashMap()).runNoReply(conn)
        }

    override fun getMessages(limit: Int?): List<CoinbaseProductMessage.GQLMessage> =
            limit?.let {
                r.table(MESSAGES).limit(it).run(conn, HashMap::class.java).map { row -> CoinbaseProductMessage.toGQLMessage(row) }
            } ?:
            r.table(MESSAGES).run(conn, HashMap::class.java).map { row -> CoinbaseProductMessage.toGQLMessage(row) }

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

    override fun storeSubscriptions(givenSubscriptions: ProductsIds) =
        r.table(PRODUCT_SUBSCRIPTIONS)
                .filter { row -> row.g("sub_id").eq("coinbase_adapter_subs") }
                .update {
                    hashMapOf("product_ids" to (getSubscriptions() union givenSubscriptions).joinToString(separator = ",") { it.value } + ",")
                }.runNoReply(conn)

    override fun getSubscriptions(): ProductsIds =
            r.table(PRODUCT_SUBSCRIPTIONS)
                    .filter { it.g("sub_id").eq("coinbase_adapter_subs") }
                    .pluck("product_ids")
                    .run(conn, HashMap::class.java).next()
                    ?.let { mapSplitStringToProductIds(it["product_ids"] as String) } ?: emptyList()

    private fun CoinbaseProductMessage.toDbHashMap() =
        hashMapOf(
            "type" to type.name,
            "sequence" to sequence.value,
            "product_id" to productId.value,
            "price" to price.value,
            "open_24h" to open24h.value,
            "volume_24h" to volume24h.value,
            "low_24h" to low24h.value,
            "high_24h" to high24h.value,
            "volume_30d" to volume30d.value,
            "best_bid" to bestBid.value,
            "best_ask" to bestAsk.value,
            "side" to side.name,
            "time" to time.toOffsetDateTimeUTC(),
            "trade_id" to tradeId.value,
            "last_size" to lastSize.value
        )

    private val mapSplitStringToProductIds: (String) -> ProductsIds = { s ->
        if (s.isBlank()) emptyList()
        else {
            s.split(",").toMutableList()
                    .apply { removeAll { it.isBlank() } }
                    .map { ProductId(it) }
        }
    }
}

private fun Instant.toOffsetDateTimeUTC() =
    OffsetDateTime.ofInstant(this, ZoneOffset.UTC)
