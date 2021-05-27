package me.pysquad.cryptobot.subscriber

import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import com.typesafe.config.ConfigFactory
import org.testcontainers.containers.GenericContainer

abstract class TestContainersBase {

    companion object {
        private const val containerRethinkDbPort = 28015

        val app: Application
        val testDbConn: Connection

        private val containerRethinkDb = GenericContainer<Nothing>("rethinkdb:2.4.0").apply {
            withExposedPorts(containerRethinkDbPort)
        }

        init {
            containerRethinkDb.start()
            testDbConn = setupRethinkDb(containerRethinkDb.getMappedPort(containerRethinkDbPort))
            setupMockCoinbaseWebsocketFeed()
            app = Application(ConfigFactory.load("application-test"))
            app.start()
        }
    }
}

private fun setupRethinkDb(port: Int): Connection {
    val conn = RethinkDB.r.connection()
        .hostname("localhost")
        .port(port)
        .connect()

    RethinkDB.r.dbDrop("test").run(conn)
    RethinkDB.r.dbCreate(CRYPTOBOT).run(conn)
    RethinkDB.r.db(CRYPTOBOT).tableCreate(COINBASE_MESSAGES).run(conn)
    RethinkDB.r.db(CRYPTOBOT).tableCreate(COINBASE_PRODUCT_SUBSCRIPTIONS).run(conn)

    System.setProperty("RETHINKDB_PORT", port.toString())

    return conn
}

private fun setupMockCoinbaseWebsocketFeed() {
    val mockCoinbaseWebsocketFeedPort = 9000
    MockCoinbaseWebsocketFeed(mockCoinbaseWebsocketFeedPort).start()
    System.setProperty("COINBASE_WS_FEED_URI", "ws://localhost:$mockCoinbaseWebsocketFeedPort")
}
