package me.pysquad.cryptobot

import com.rethinkdb.gen.exc.ReqlOpFailedError
import me.pysquad.cryptobot.coinbase.ProductId
import me.pysquad.cryptobot.coinbase.ProductsIds
import org.http4k.core.Credentials

interface CoinbaseAdapterRepository {
    fun getCoinbaseSandboxApiCredentials(): CoinbaseSandboxApiCredentials
    fun getCoinbaseAdapterAuthCredentials(): Credentials
}

class CoinbaseAdapterRepoImpl(realTimeDb: RealTimeDb): CoinbaseAdapterRepository {
    private val r = realTimeDb.rethinkCtx
    private val conn = realTimeDb.connection

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
}