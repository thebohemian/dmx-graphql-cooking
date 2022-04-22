package systems.dmx.cooking.schema

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query

/**
 * Provide search and query options for baking ingredients.
 */
class BakingIngredientService : Query {

    @GraphQLDescription("Just return a very secure random number")
    fun getSecureRandomNumber(): Int = 4

    fun hello() = "World!"

}
