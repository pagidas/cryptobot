package me.pysquad.cryptobot.repo

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource
import javax.inject.Singleton

interface MessagesRepo {

}

@Singleton
class MessagesRepoImpl(rethinkDbDatasource: RethinkDbDatasource): MessagesRepo {

    private val conn: Connection = rethinkDbDatasource.connection
    private val r: RethinkDB = rethinkDbDatasource.ctx

}
