package me.pysquad.cryptobot

import com.rethinkdb.RethinkDB
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.gen.exc.ReqlOpFailedError
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvBasicAuthPassword
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvBasicAuthUsername
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvCoinbaseSandboxApiKey
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvCoinbaseSandboxApiPassphrase
import me.pysquad.cryptobot.EnvHelper.Companion.getEnvCoinbaseSandboxApiSecret
import me.pysquad.cryptobot.security.SecurityProvider.Companion.toSHA256
import me.pysquad.cryptobot.common.MissingEnvVariableException
import me.pysquad.cryptobot.config.DbConfig

const val CRYPTOBOT = "cryptobot"
const val MESSAGES = "messages"
const val API_KEYS = "api_keys"
const val COINBASE_ADAPTER_AUTH = "coinbase_adapter_auth"

object RealTimeDb {
    val rethinkCtx: RethinkDB = r
    var connection: Connection? = null

    /**
     * For now, [connection] contains the [CRYPTOBOT] db by default.
     * We can either use another database when querying by passing
     * in the connection a different db like so connection.use("db-name")
     * or change the default database when connecting.
     */
    fun connect(dbConfig: DbConfig, fn: RealTimeDb.() -> Unit) = apply {
        connection = rethinkCtx.connection().hostname(dbConfig.host).port(dbConfig.port).db(CRYPTOBOT).connect()
        fn()
    }

    fun close() = connection?.close()

    /**
     * Call this function before starting the app so the specific tables are there.
     */
    fun runMigrations() {
        try {
            topLevelMigration()
            migrateApiKeys()
            migrateAppAuth()
        } catch (e: RuntimeException) {
            println(e.message)
        }
    }

    // --- Migration functions ---

    @Throws(ReqlOpFailedError::class)
    private fun topLevelMigration() {
        rethinkCtx.dbDrop("test").run(connection)
        rethinkCtx.dbCreate(CRYPTOBOT).run(connection)
        rethinkCtx.tableCreate(MESSAGES).run(connection)
    }

    @Throws(MissingEnvVariableException::class, ReqlOpFailedError::class)
    private fun migrateApiKeys() {
        rethinkCtx.tableCreate(API_KEYS).run(connection)
        rethinkCtx.table(API_KEYS).insert(
            hashMapOf(
                "api_id" to "cb_sandbox_api_creds",
                "coinbase_sandbox_api_key" to getEnvCoinbaseSandboxApiKey(),
                "coinbase_sandbox_api_secret" to getEnvCoinbaseSandboxApiSecret(),
                "coinbase_sandbox_api_passphrase" to getEnvCoinbaseSandboxApiPassphrase()
            )
        ).run(connection)
    }

    @Throws(MissingEnvVariableException::class, ReqlOpFailedError::class)
    private fun migrateAppAuth() {
        rethinkCtx.tableCreate(COINBASE_ADAPTER_AUTH).run(connection)
        rethinkCtx.table(COINBASE_ADAPTER_AUTH).insert(
            hashMapOf(
                    "auth_id" to "basic_auth",
                "username" to getEnvBasicAuthUsername().toSHA256(),
                "password" to getEnvBasicAuthPassword().toSHA256()
            )
        ).run(connection)
    }
}
