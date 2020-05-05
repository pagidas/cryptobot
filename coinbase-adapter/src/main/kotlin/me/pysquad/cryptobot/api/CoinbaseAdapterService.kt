package me.pysquad.cryptobot.api

import me.pysquad.cryptobot.api.coinbase.CoinbaseProductMessage

class CoinbaseAdapterService(private val coinbaseWsMessagesRepo: CoinbaseWsMessagesRepository) {

    fun storeMessages(messages: List<CoinbaseProductMessage>) = coinbaseWsMessagesRepo.store(messages)
}