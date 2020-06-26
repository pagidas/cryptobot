package me.pysquad.cryptobot.endpoints

import com.apurebase.kgraphql.KGraphQL
import me.pysquad.cryptobot.CoinbaseAdapterService
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status

class GraphQL(private val coinbaseAdapter: CoinbaseAdapterService): Endpoint {
    private val schema = KGraphQL.schema {
        configure { useDefaultPrettyPrinter = true }

        query("messages") {
            resolver { ->
                coinbaseAdapter.getMessagesGQL()
            }
        }
    }

    override val contractRoute: ContractRoute =
            "/graphql" meta {
                summary = "This is a GraphQL endpoint to fetch messages."
                security = SecurityProvider.basicAuth()
            } bindContract POST to handler()

    private fun handler(): HttpHandler = { req: Request ->
        println(req.bodyString())

        val res = schema.runCatching {
            executeBlocking(req.bodyString())
        }

        val response = res.fold(
                onSuccess = { it },
                onFailure = {
                    it.printStackTrace()
                    """ 
                    {
                        "errors": "${it.message}"
                    }
                    """.trimIndent()
                }
        )
        println(response)

        Response(Status.OK).body(response)
    }
}