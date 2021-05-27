package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import org.assertj.core.api.Assertions.assertThat
import org.awaitility.Awaitility.await
import org.http4k.client.ApacheClient
import org.http4k.core.*
import org.http4k.filter.ClientFilters
import org.http4k.filter.DebuggingFilters
import org.http4k.format.Jackson.auto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.function.Predicate

class IntegrationTests: TestContainersBase() {

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Health {

        private val testHttpClient: HttpHandler

        init {
            testHttpClient = with(app.appConfig) {
                DebuggingFilters.PrintRequestAndResponse()
                    .then(
                        ClientFilters.SetBaseUriFrom(Uri.of("http://$host:$port"))
                            .then(ApacheClient())) }
        }

        @Test
        fun `application should be up and running`() {
            val lens = Body.auto<Map<String, String>>().toLens()

            val response = testHttpClient(Request(Method.GET, "/health"))
            val health = lens(response)

            await().untilAsserted {
                assertThat(health).extracting("status").isEqualTo("UP")
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class Subscriber {

        private val testHttpClient: HttpHandler

        init {
            testHttpClient = with(app.appConfig) {
                DebuggingFilters.PrintRequestAndResponse()
                    .then(
                        ClientFilters.SetBaseUriFrom(Uri.of("http://$host:$port"))
                            .then(ApacheClient())) }
        }

        @AfterEach
        fun cleanup() {
            RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).delete().run(testDbConn)
            RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).delete().run(testDbConn)

            val subscriptions = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_PRODUCT_SUBSCRIPTIONS).run(testDbConn).toList()
            val messages = RethinkDB.r.db(CRYPTOBOT).table(COINBASE_MESSAGES).run(testDbConn).toList()

            await().untilAsserted {
                assertThat(subscriptions).isEmpty()
                assertThat(messages).isEmpty()
            }
        }

        @Test
        fun `should subscribe to coinbase websocket feed`() {
            val response = testHttpClient(
                Request(Method.POST, "/coinbase-product-subscriptions")
                    .body("""{ "productId": "BTC-EUR" }""".trimIndent()))

            assertThat(response.status).isEqualTo(Status.ACCEPTED)


            await().untilAsserted {
                assertThat(
                    RethinkDB.r.db(CRYPTOBOT)
                        .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                        .run(testDbConn)
                        .toList()
                ).hasSize(1).extracting("channel").allMatch(Predicate.isEqual("ticker"))

                assertThat(
                    RethinkDB.r.db(CRYPTOBOT)
                        .table(COINBASE_MESSAGES)
                        .run(testDbConn)
                        .toList()
                ).isNotEmpty.extracting("type").allMatch(Predicate.isEqual("ticker"))
            }
        }

        @Test
        fun `should subscribe to coinbase websocket feed only once`() {
            app.subscriberService.subscriptions.clear()

            val responseA = testHttpClient(
                Request(Method.POST, "/coinbase-product-subscriptions")
                    .body("""{ "productId": "ETH-EUR" }""".trimIndent()))

            assertThat(responseA.status).isEqualTo(Status.ACCEPTED)

            await().untilAsserted {
                val responseB = testHttpClient(
                    Request(Method.POST, "/coinbase-product-subscriptions")
                        .body("""{ "productId": "ETH-EUR" }""".trimIndent()))


                assertThat(responseB.status).isEqualTo(Status.CONFLICT)
            }

            await().untilAsserted {
                val subscriptions = RethinkDB.r
                    .db(CRYPTOBOT)
                    .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                    .run(testDbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                    .toList()

                assertThat(subscriptions).isNotEmpty.extracting("productId").allMatch(Predicate.isEqual("ETH-EUR"))
            }
        }

        @Test
        fun `should not subscribe to coinbase to a unknown product id`() {
            val response = testHttpClient(
                Request(Method.POST, "/coinbase-product-subscriptions")
                    .body("""{ "productId": "unknown-product-id" }""".trimIndent()))

            assertThat(response.status).isEqualTo(Status.ACCEPTED)

            await().untilAsserted {
                val subscriptions =
                    RethinkDB.r.db(CRYPTOBOT)
                        .table(COINBASE_PRODUCT_SUBSCRIPTIONS)
                        .run(testDbConn, object: TypeReference<CoinbaseProductSubscription>() {})
                        .toList()

                assertThat(subscriptions).isEmpty()
            }
        }
    }
}
