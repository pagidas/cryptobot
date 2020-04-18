package me.pysquad.cryptobot.api

class CryptobotService(private val coinbaseWsMessagesRepo: CoinbaseWsMessagesRepository) {

    fun storeMessages() = coinbaseWsMessagesRepo.store()
}