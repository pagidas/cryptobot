package me.pysquad.cryptobot

class CryptobotService(private val coinbaseWsMessagesRepo: CoinbaseWsMessagesRepository) {

    fun storeMessages() = coinbaseWsMessagesRepo.store()
}