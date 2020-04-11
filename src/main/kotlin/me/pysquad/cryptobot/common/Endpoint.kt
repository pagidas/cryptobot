package me.pysquad.cryptobot.common

import org.http4k.contract.ContractRoute

interface Endpoint {
    val contractRoute: ContractRoute
}