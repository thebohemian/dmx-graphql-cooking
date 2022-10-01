package systems.dmx.cooking.schema

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import systems.dmx.core.service.CoreService

/**
 * Provide search and query options for baking ingredients.
 */
class DemoService : Query {

    @GraphQLDescription("Just return a very secure random number")
    fun getSecureRandomNumber(): Int = 4

    fun hello() = "World!"

    fun getById(dfe: DataFetchingEnvironment, id: String): String =
        dmx(dfe).getTopic(id.toLong()).simpleValue.toString()

    fun getAll(dfe: DataFetchingEnvironment): List<String> =
        dmx(dfe).getTopicsByType("dmx.cooking.baking_ingredient").map {
            it.simpleValue.toString()
        }

    fun cooking() = CookingQuery()

    private fun dmx(dfe: DataFetchingEnvironment) = dfe.graphQlContext.get<CoreService>("dmx")
}
