package me.pysquad.cryptobot.subscriber

import com.fasterxml.jackson.core.type.TypeReference
import com.rethinkdb.RethinkDB
import com.rethinkdb.net.Connection

interface SubscriberRepository {
    fun storeMessage(message: CoinbaseMessage)
    fun storeSubscription(subscription: CoinbaseProductSubscriptionV2)
    fun getSubscriptions(): List<CoinbaseProductSubscriptionV2>

    companion object {
        fun impl(rethinkDbDatasource: RethinkDbDatasource) = object: SubscriberRepository {

            private val conn: Connection = rethinkDbDatasource.connection
            private val r: RethinkDB = rethinkDbDatasource.ctx

            override fun storeMessage(message: CoinbaseMessage) =
                r.table(COINBASE_MESSAGES).insert(message).runNoReply(conn)

            override fun getSubscriptions(): List<CoinbaseProductSubscriptionV2> =
                r.table(COINBASE_PRODUCT_SUBSCRIPTIONS).run(conn, object: TypeReference<CoinbaseProductSubscriptionV2>() {}).toList()

            override fun storeSubscription(subscription: CoinbaseProductSubscriptionV2) =
                r.table(COINBASE_PRODUCT_SUBSCRIPTIONS).insert(subscription).runNoReply(conn)
        }
    }
}