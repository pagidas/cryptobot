package me.pysquad.cryptobot.endpoints

import com.apurebase.kgraphql.KGraphQL
import me.pysquad.cryptobot.CoinbaseAdapterService
import me.pysquad.cryptobot.common.Endpoint
import me.pysquad.cryptobot.security.SecurityProvider
import org.http4k.contract.ContractRoute
import org.http4k.contract.meta
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Query
import org.http4k.lens.string

class GraphQL(private val coinbaseAdapter: CoinbaseAdapterService): Endpoint {
    private val queryParamLens = Query.string().required("query", "The query that GraphQL wants to know what data to fetch.")

    private val schema = KGraphQL.schema {
        configure { useDefaultPrettyPrinter = true }

        query("messages") {
            resolver { limit: Int? ->
                coinbaseAdapter.getMessagesGQL(limit)
            }
        }
    }

    override val contractRoute: ContractRoute =
            "/graphql" meta {
                summary = "This is a GraphQL endpoint to fetch messages."
                queries += queryParamLens
                security = SecurityProvider.basicAuth()
            } bindContract GET to handler()

    private fun handler(): HttpHandler = { req: Request ->
        val resp = schema.runCatching { executeBlocking(queryParamLens(req)) }
                .fold(
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
        Response(OK).body(resp)
    }
}