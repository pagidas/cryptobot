package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.service.SubscriberService

fun subscriberRoutes(service: SubscriberService) = mutableListOf(
        subscribe(service)
)