package me.pysquad.cryptobot.subscriber.repo

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection
import me.pysquad.cryptobot.subscriber.model.CoinbaseMessage
import me.pysquad.cryptobot.subscriber.model.ProductIds
import me.pysquad.cryptobot.subscriber.repo.model.ProductSubscriptionDto
import me.pysquad.cryptobot.subscriber.rethinkdb.MESSAGES
import me.pysquad.cryptobot.subscriber.rethinkdb.PRODUCT_SUBSCRIPTIONS
import me.pysquad.cryptobot.subscriber.rethinkdb.RethinkDbDatasource

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