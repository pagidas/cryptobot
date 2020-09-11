package me.pysquad.cryptobot.graphql

import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import javax.inject.Singleton

@Singleton
class HelloDataFetcher : DataFetcher<String> {

    override fun get(env: DataFetchingEnvironment): String {
        val name = env.getArgument<String?>("name")

        return name?.let {
            "Hello $it!"

        } ?: "Hello World!"
    }
}