package systems.dmx.cooking.graphql

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import graphql.GraphQL
import graphql.schema.*
import systems.dmx.cooking.schema.DemoService


fun createRequestHandler(schema: GraphQLSchema) = GraphQLRequestHandler(
    graphQL = GraphQL.Builder(schema).build()
)

@OptIn(ExperimentalStdlibApi::class)
fun generateSchema() = SchemaGenerator(
    SchemaGeneratorConfig(
        supportedPackages = listOf("systems.dmx.cooking.schema"),
        hooks = NoopSchemaGeneratorHooks
    )
).generateSchema(
    queries = listOf(TopLevelObject(DemoService())),
    mutations = emptyList()
)
