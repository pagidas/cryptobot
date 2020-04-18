package me.pysquad.cryptobot.api

import me.pysquad.cryptobot.Database

interface CoinbaseWsMessagesRepository {
    fun store()
}

class CoinbaseWsMessagesRepo(private val db: Database):
    CoinbaseWsMessagesRepository {
    override fun store() {
        println("Hello from REPO!")
    }
}