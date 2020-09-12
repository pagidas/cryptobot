package me.pysquad.cryptobot.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import me.pysquad.cryptobot.model.CoinbaseMessage
import me.pysquad.cryptobot.repo.MessagesRepoImpl
import javax.inject.Singleton

@Singleton
class CoinbaseDataFetcher(private val messagesRepoImpl: MessagesRepoImpl) : DataFetcher<List<CoinbaseMessage>?> {

    override fun get(env: DataFetchingEnvironment): List<CoinbaseMessage>? {
        return messagesRepoImpl.getCoinbaseMessages()
    }
}