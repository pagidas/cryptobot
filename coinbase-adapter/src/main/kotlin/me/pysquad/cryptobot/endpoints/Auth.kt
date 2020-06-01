package me.pysquad.cryptobot.endpoints

import me.pysquad.cryptobot.AuthenticateRequest
import me.pysquad.cryptobot.AuthenticateResponse
import me.pysquad.cryptobot.security.SecurityProvider
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.common.Error
import me.pysquad.cryptobot.common.ErrorResponse
import me.pysquad.cryptobot.common.withErrorResponse
import org.http4k.contract.meta
import org.http4k.core.Credentials
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.Status.Companion.UNAUTHORIZED
import org.http4k.core.with

class Auth(private val securityProvider: SecurityProvider): Endpoint {
    private val exampleAuthenticateRequest = AuthenticateRequest(credentials = Credentials("username", "password"))
    private val exampleAuthenticateResponse = AuthenticateResponse(authToken = "ZnJlZDpmcmVk")
    private val exampleErrorResponse = ErrorResponse(Error("401 - Unauthorized", "Wrong Credentials"))

    override val contractRoute =
            "/auth" meta {
                summary = "Authenticates to coinbase-adapter api."
                receiving(AuthenticateRequest.lens to exampleAuthenticateRequest)
                returning(OK, AuthenticateResponse.lens to exampleAuthenticateResponse)
                returning(UNAUTHORIZED, ErrorResponse.lens to exampleErrorResponse)
            } bindContract POST to handler()

    private fun handler(): HttpHandler = { req: Request ->
        with(AuthenticateRequest.lens(req)) {
            securityProvider.getAuthToken(credentials)?.let {
                Response(OK).with(AuthenticateResponse.lens of AuthenticateResponse(it))
            } ?: Response(UNAUTHORIZED).withErrorResponse("Wrong Credentials")
        }
    }
}