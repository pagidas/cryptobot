package me.pysquad.cryptobot

import com.rethinkdb.RethinkDB
import com.rethinkdb.RethinkDB.r
import com.rethinkdb.gen.ast.Db
import com.rethinkdb.gen.exc.ReqlOpFailedError
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.RealTimeDb.rethinkCtx
import me.pysquad.cryptobot.config.DbConfig

private const val CRYPTOBOT = "cryptobot"
private const val MESSAGES = "messages"
private val cryptoDb = rethinkCtx.db(CRYPTOBOT)

fun inTransaction(db: Db = cryptoDb, fn: Db.() -> Unit = {}) = db.fn()

object RealTimeDb {
    val rethinkCtx: RethinkDB = r
    var connection: Connection? = null

    fun connect(dbConfig: DbConfig) = apply {
        connection = rethinkCtx.connection().hostname(dbConfig.host).port(dbConfig.port).connect()
    }

    /**
     * Required db and table in RethinkDB so that the app can run.
     */
    fun runMigrations() {
        try {
            rethinkCtx.dbCreate(CRYPTOBOT).run(connection)
            inTransaction {
                tableCreate(MESSAGES).run(connection)
            }
        } catch (e: ReqlOpFailedError) {
            println(e.message)
        }
    }

    fun close() = connection?.close()
}
