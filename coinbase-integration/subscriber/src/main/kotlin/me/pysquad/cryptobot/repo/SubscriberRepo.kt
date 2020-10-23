package me.pysquad.cryptobot.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.model.CoinbaseMessage
import me.pysquad.cryptobot.model.ProductIds
import me.pysquad.cryptobot.repo.model.ProductSubscriptionDto
import me.pysquad.cryptobot.rethinkdb.MESSAGES
import me.pysquad.cryptobot.rethinkdb.PRODUCT_SUBSCRIPTIONS
import me.pysquad.cryptobot.rethinkdb.RethinkDbDatasource

interface SubscriberRepo {
    fun storeMessages(messages: List<CoinbaseMessage>)
    fun getSubscriptions(): ProductIds
    fun storeSubscriptions(givenSubscriptions: ProductIds)

    companion object {
        fun impl(rethinkDbDatasource: RethinkDbDatasource) = object: SubscriberRepo {

            private val conn: Connection = rethinkDbDatasource.connection
            private val r: RethinkDB = rethinkDbDatasource.ctx

            override fun storeMessages(messages: List<CoinbaseMessage>) =
                r.table(MESSAGES).insert(messages).runNoReply(conn)

            override fun getSubscriptions(): ProductIds =
                    r.table(PRODUCT_SUBSCRIPTIONS)
                            .filter { row -> row.g("sub_id").eq("coinbase-pro-subscription") }
                            .run(conn, object: TypeReference<ProductSubscriptionDto>() {})
                            .next()
                            ?.productIdsAsList ?: emptyList()

            override fun storeSubscriptions(givenSubscriptions: ProductIds) =
                    r.table(PRODUCT_SUBSCRIPTIONS)
                            .filter { row -> row.g("sub_id").eq("coinbase-pro-subscription") }
                            .update {
                                ProductSubscriptionDto(
                                        "coinbase-pro-subscription",
                                        (getSubscriptions() union givenSubscriptions)
                                                .joinToString(separator = ",") + ",")
                            }
                            .runNoReply(conn)
        }
    }
}