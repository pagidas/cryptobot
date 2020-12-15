package me.pysquad.cryptobot.subscriber.endpoints

import me.pysquad.cryptobot.subscriber.service.SubscriberService

fun subscriberRoutes(service: SubscriberService) = mutableListOf(
        subscribe(service)
)