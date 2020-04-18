package me.pysquad.cryptobot

interface CoinbaseWsMessagesRepository {
    fun store()
}

class CoinbaseWsMessagesRepo(private val db: Database): CoinbaseWsMessagesRepository {
    override fun store() {
        println("Hello from REPO!")
    }
}