package me.pysquad.cryptobot.graphql

import graphql.GraphQL
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceResolver
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Singleton

@Factory
class GraphQLFactory {

    @Bean
    @Singleton
    fun graphQL(resourceResolver: ResourceResolver, coinbaseDataFetcher: CoinbaseDataFetcher): GraphQL {

        // Parse the schema.
        val typeRegistry = TypeDefinitionRegistry().merge(SchemaParser().parse(BufferedReader(InputStreamReader(
                resourceResolver.getResourceAsStream("classpath:schema.graphql").get()))))

        // Create the runtime wiring.
        val runtimeWiring = RuntimeWiring.newRuntimeWiring()
                .type("Query") { it.dataFetcher("hello", coinbaseDataFetcher) }
                .build()

        // Create the executable schema.
        return GraphQL.newGraphQL(
                SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring)
        ).build()
    }
}