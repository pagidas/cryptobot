package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters.SetBaseUriFrom
import org.http4k.filter.DebuggingFilters
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.function.Predicate

@Testcontainers
class ApplicationIT: AbstractContainerBase() {

    private val httpClient: HttpHandler = with(testApp.appConfig) {
        DebuggingFilters.PrintRequestAndResponse()
            .then(
                SetBaseUriFrom(Uri.of("http://$host:$port"))
                    .then(ApacheClient())) }

    companion object {
        private lateinit var testApp: Application
        // keep dbConn alive and close after all -- used to query if data is in rethinkDb
        private lateinit var dbConn: Connection

        private const val mockCoinbaseWebsocketFeedPort = 9000
        private const val mockCoinbaseWebsocketUri = "ws://localhost:${mockCoinbaseWebsocketFeedPort}"
        private val mockCoinbaseWebsocketFeed = MockCoinbaseWebsocketFeed(mockCoinbaseWebsocketFeedPort)

        @BeforeAll
        @JvmStatic
        internal fun init() {
            assertThat(containerRethinkDb.isRunning).isTrue
            setupRethinkDb(exposedContainerRethinkDbPort)
            mockCoinbaseWebsocketFeed.start()
            setEnvVars()
            setupApp(ConfigFactory.load("application-test"))
        }

        @AfterAll
        @JvmStatic
        internal fun tearDown() {
            dbConn.close()
            mockCoinbaseWebsocketFeed.stop()
        }

        private fun setEnvVars() {
            System.setProperty("RETHINKDB_PORT", exposedContainerRethinkDbPort.toString())
            System.setProperty("COINBASE_WS_FEED_URI", mockCoinbaseWebsocketUri)
        }

        private fun setupApp(config: Config) {
            testApp = Application(config)
            testApp.start()
        }

        private fun setupRethinkDb(port: Int) {
            dbConn = RethinkDB.r.connection()
                .hostname("localhost")
                .port(port)
                .connect()

            RethinkDB.r.dbDrop("test").run(dbConn)
            RethinkDB.r.dbCreate(CRYPTOBOT).run(dbConn)
            RethinkDB.r.db(CRYPTOBOT).tableCreate(COINBASE_MESSAGES).run(dbConn)
            RethinkDB.r.db(CRYPTOBOT).tableCreate(COINBASE_PRODUCT_SUBSCRIPTIONS).run(dbConn)

            val messages = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).run(dbConn).toList()
            val subscriptions = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).run(dbConn).toList()
            assertThat(messages).isEmpty()
            assertThat(subscriptions).isEmpty()
        }
    }

    @AfterEach
    internal fun cleanup() {
        RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).delete().run(dbConn)
        RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).delete().run(dbConn)

        val subscriptions = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).run(dbConn).toList()
        val messages = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).run(dbConn).toList()

        Awaitility.await().untilAsserted {
            assertThat(subscriptions).isEmpty()
            assertThat(messages).isEmpty()
        }
    }

    @Test
    fun `should subscribe to coinbase websocket feed`() {
        val response = httpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "BTC-EUR" }""".trimIndent()))

        assertThat(response.status).isEqualTo(Status.ACCEPTED)


        Awaitility.await().untilAsserted {
            assertThat(
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                    .run(dbConn)
                    .toList())
                .hasSize(1).extracting("channel").allMatch(Predicate.isEqual("ticker"))

            assertThat(
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_MESSAGES)
                    .run(dbConn)
                    .toList())
                .isNotEmpty.extracting("type").allMatch(Predicate.isEqual("ticker"))
        }
    }

    @Test
    fun `should subscribe to coinbase websocket feed only once`() {
        testApp.subscriberService.subscriptions.clear()

        val responseA = httpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "ETH-EUR" }""".trimIndent()))

        assertThat(responseA.status).isEqualTo(Status.ACCEPTED)

        Awaitility.await().untilAsserted {
            val responseB = httpClient(
                Request(Method.POST, "/coinbase-product-subscriptions")
                    .body("""{ "productId": "ETH-EUR" }""".trimIndent()))


            assertThat(responseB.status).isEqualTo(Status.CONFLICT)
        }

        Awaitility.await().untilAsserted {
            val subscriptions = RethinkDB.r
                .db(CRYPTOBOT)
                .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                .run(dbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                .toList()

            assertThat(subscriptions).isNotEmpty.extracting("productId").allMatch(Predicate.isEqual("ETH-EUR"))
        }
    }

    @Test
    fun `should not subscribe to coinbase to a unknown product id`() {
        val response = httpClient(
            Request(Method.POST, "/coinbase-product-subscriptions")
                .body("""{ "productId": "unknown-product-id" }""".trimIndent()))

        assertThat(response.status).isEqualTo(Status.ACCEPTED)

        Awaitility.await().untilAsserted {
            val subscriptions =
                RethinkDB.r.db(CRYPTOBOT)
                    .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                    .run(dbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                    .toList()

            assertThat(subscriptions).isEmpty()
        }
    }
}