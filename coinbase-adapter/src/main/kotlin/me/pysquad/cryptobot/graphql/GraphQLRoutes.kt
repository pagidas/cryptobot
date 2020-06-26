package me.pysquad.cryptobot.graphql

import com.apurebase.kgraphql.KGraphQL
import me.pysquad.cryptobot.CoinbaseAdapterService
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.RoutingHttpHandler
import org.http4k.routing.bind
import org.http4k.routing.routes

object GraphQLRoute {
    operator fun invoke(coinbaseAdapter: CoinbaseAdapterService): RoutingHttpHandler {
        val schema = KGraphQL.schema {
            configure {
                useDefaultPrettyPrinter = true
            }

            query("messages") {
                resolver { ->
                    coinbaseAdapter.getMessagesGQL()
                }
            }
        }

        return routes(
                "/api/fetcher" bind POST to { req: Request ->
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
                    Response(OK).body(response)
                }
        )
    }
}