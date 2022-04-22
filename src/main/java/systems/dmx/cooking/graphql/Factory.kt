import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.hooks.NoopSchemaGeneratorHooks
import com.expediagroup.graphql.server.execution.GraphQLRequestHandler
import graphql.GraphQL
import systems.dmx.cooking.schema.BakingIngredientService

fun createRequestHandler() = GraphQLRequestHandler(
        graphQL = GraphQL.Builder(SchemaGenerator(SchemaGeneratorConfig(
                supportedPackages = listOf("systems.dmx.cooking.schema"),
                hooks = NoopSchemaGeneratorHooks)).generateSchema(
                queries = listOf(TopLevelObject(BakingIngredientService())),
                mutations = emptyList())).build()
)
