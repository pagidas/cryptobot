package me.pysquad.cryptobot.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.model.CoinbaseMessage
import me.pysquad.cryptobot.rethinkdb.MESSAGES
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource
import javax.inject.Singleton

interface MessagesRepo {
    fun getCoinbaseMessages(): List<CoinbaseMessage>?
    fun getMostRecentCoinbaseMessages(mostRecent: Int): List<CoinbaseMessage>?
}

@Singleton
class MessagesRepoImpl(rethinkDbDatasource: RethinkDbDatasource): MessagesRepo {

    private val conn: Connection = rethinkDbDatasource.connection
    private val r: RethinkDB = rethinkDbDatasource.ctx

    override fun getCoinbaseMessages(): List<CoinbaseMessage>? =
        r.table(MESSAGES).run(conn, object: TypeReference<CoinbaseMessage>() {}).toList()

    override fun getMostRecentCoinbaseMessages(mostRecent: Int): List<CoinbaseMessage>? =
        r.table(MESSAGES)
                .orderBy(r.desc("time"))
                .limit(mostRecent)
                .run(conn, object : TypeReference<List<CoinbaseMessage>>() {})
                .next()
}
