package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection

interface SubscriberRepository {
    fun storeMessages(messages: List<CoinbaseMessage>)
    fun getSubscriptions(): ProductIds
    fun storeSubscriptions(givenSubscriptions: ProductIds)

    companion object {
        fun impl(rethinkDbDatasource: RethinkDbDatasource) = object: SubscriberRepository {

            private val conn: Connection = rethinkDbDatasource.connection
            private val r: RethinkDB = rethinkDbDatasource.ctx

            override fun storeMessages(messages: List<CoinbaseMessage>) =
                r.table(MESSAGES).insert(messages).runNoReply(conn)

            override fun getSubscriptions(): ProductIds =
                    r.table(PRODUCT_SUBSCRIPTIONS)
                            .filter { row -> row.g("sub_id").eq("coinbase-pro-subscription") }
                            .run(conn, object: TypeReference<CoinbaseProductSubscription>() {})
                            .next()
                            ?.productIdsAsList ?: emptyList()

            override fun storeSubscriptions(givenSubscriptions: ProductIds) =
                    r.table(PRODUCT_SUBSCRIPTIONS)
                            .filter { row -> row.g("sub_id").eq("coinbase-pro-subscription") }
                            .update {
                                CoinbaseProductSubscription(
                                        "coinbase-pro-subscription",
                                        (getSubscriptions() union givenSubscriptions)
                                                .joinToString(separator = ",") + ",")
                            }
                            .runNoReply(conn)
        }
    }
}