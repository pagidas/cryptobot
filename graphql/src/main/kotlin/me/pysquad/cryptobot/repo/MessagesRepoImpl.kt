package me.pysquad.cryptobot.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.model.CoinbaseMessage
import me.pysquad.cryptobot.rethinkdb.MESSAGES
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource
import javax.inject.Singleton

interface MessagesRepo {
 fun getCoinbaseMessages(): List<CoinbaseMessage>?
}

@Singleton
class MessagesRepoImpl(rethinkDbDatasource: RethinkDbDatasource): MessagesRepo {

    private val conn: Connection = rethinkDbDatasource.connection
    private val r: RethinkDB = rethinkDbDatasource.ctx

    // FIXME: 11/09/2020 Not deserializing properly...
    override fun getCoinbaseMessages(): List<CoinbaseMessage>? {
        val result = r.table(MESSAGES).run(conn, object: TypeReference<List<CoinbaseMessage>>() {}).apply(::println)
        return ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .readValue("result", object: TypeReference<List<CoinbaseMessage>>() {})
    }
}
