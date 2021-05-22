package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import com.typesafe.config.ConfigFactory
import org.assertj.core.api.Assertions
import org.awaitility.Awaitility
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.function.Predicate

@Testcontainers
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ApplicationIT: TestContainersBase() {

    private val app: Application
    private val testDbConn: Connection
    private val testHttpClient: HttpHandler

    init {
        containerRethinkDb.start()
        testDbConn = setupRethinkDb(exposedContainerRethinkDbPort)
        setupMockCoinbaseWebsocketFeed()
        app = Application(ConfigFactory.load("application-test"))
        app.start()

        testHttpClient = with(app.appConfig) {
            DebuggingFilters.PrintRequestAndResponse()
                .then(
                    ClientFilters.SetBaseUriFrom(Uri.of("http://$host:$port"))
                        .then(ApacheClient())) }
    }

    @AfterEach
    internal fun cleanup() {
        RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).delete().run(testDbConn)
        RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).delete().run(testDbConn)

        val subscriptions = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).run(testDbConn).toList()
        val messages = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).run(testDbConn).toList()

        Awaitility.await().untilAsserted {
            Assertions.assertThat(subscriptions).isEmpty()
            Assertions.assertThat(messages).isEmpty()
        }
    }

    @Test
    fun `should subscribe to coinbase websocket feed`() {
        val response = testHttpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "BTC-EUR" }""".trimIndent()))

        Assertions.assertThat(response.status).isEqualTo(Status.ACCEPTED)


        Awaitility.await().untilAsserted {
            Assertions.assertThat(
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                    .run(testDbConn)
                    .toList()
            )
                .hasSize(1).extracting("channel").allMatch(Predicate.isEqual("ticker"))

            Assertions.assertThat(
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_MESSAGES)
                    .run(testDbConn)
                    .toList()
            )
                .isNotEmpty.extracting("type").allMatch(Predicate.isEqual("ticker"))
        }
    }

    @Test
    fun `should subscribe to coinbase websocket feed only once`() {
        app.subscriberService.subscriptions.clear()

        val responseA = testHttpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "ETH-EUR" }""".trimIndent()))

        Assertions.assertThat(responseA.status).isEqualTo(Status.ACCEPTED)

        Awaitility.await().untilAsserted {
            val responseB = testHttpClient(
                Request(Method.POST, "/coinbase-product-subscriptions")
                    .body("""{ "productId": "ETH-EUR" }""".trimIndent()))


            Assertions.assertThat(responseB.status).isEqualTo(Status.CONFLICT)
        }

        Awaitility.await().untilAsserted {
            val subscriptions = RethinkDB.r
                .db(CRYPTOBOT)
                .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                .run(testDbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                .toList()

            Assertions.assertThat(subscriptions).isNotEmpty.extracting("productId").allMatch(Predicate.isEqual("ETH-EUR"))
        }
    }

    @Test
    fun `should not subscribe to coinbase to a unknown product id`() {
        val response = testHttpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "unknown-product-id" }""".trimIndent()))

        Assertions.assertThat(response.status).isEqualTo(Status.ACCEPTED)

        Awaitility.await().untilAsserted {
            val subscriptions =
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                    .run(testDbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                    .toList()

            Assertions.assertThat(subscriptions).isEmpty()
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
