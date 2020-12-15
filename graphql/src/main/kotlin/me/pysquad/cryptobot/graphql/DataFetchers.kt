package me.pysquad.cryptobot.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import me.pysquad.cryptobot.graphql.model.CoinbaseMessage
import me.pysquad.cryptobot.graphql.model.ProductIds
import me.pysquad.cryptobot.graphql.repo.MessagesRepoImpl
import javax.inject.Singleton

@Singleton
class MessagesDataFetcher(private val messagesRepoImpl: MessagesRepoImpl) : DataFetcher<List<CoinbaseMessage>?> {

    override fun get(env: DataFetchingEnvironment): List<CoinbaseMessage>? =
        env.getArgument<Int>("mostRecent")?.let {
            messagesRepoImpl.getMostRecentCoinbaseMessages(it)
        } ?:
        messagesRepoImpl.getCoinbaseMessages()
}

@Singleton
class ProductSubscriptionsDataFetcher(private val messagesRepoImpl: MessagesRepoImpl) : DataFetcher<ProductIds> {

    override fun get(env: DataFetchingEnvironment): ProductIds =
            messagesRepoImpl.getProductSubscriptions()
}