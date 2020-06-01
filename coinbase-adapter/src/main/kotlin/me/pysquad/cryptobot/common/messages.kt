package me.pysquad.cryptobot.common

import me.pysquad.cryptobot.CoinbaseAdapterJson.auto
import org.http4k.core.Body
import org.http4k.core.Response
import org.http4k.core.with

data class Error(val code: String, val message: String)
data class ErrorResponse(val error: Error) {
    companion object {
        val lens = Body.auto<ErrorResponse>().toLens()
    }
}

fun Response.withErrorResponse(message: String) =
        this.with(ErrorResponse.lens of ErrorResponse(Error("${status.code} - ${status.description}", message)))