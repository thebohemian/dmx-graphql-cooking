package systems.dmx.cooking.schema

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import systems.dmx.core.Topic
import systems.dmx.core.service.CoreService

/**
 * Provide search and query options for baking ingredients.
 */
class CookingQuery : Query {

    fun bakingIngredient(dfe: DataFetchingEnvironment, id: ID) =
        instanceQuery(dfe, BAKING_INGREDIENT, id, ::BakingIngredient)

    fun bakingIngredients(dfe: DataFetchingEnvironment) =
        collectionQuery(dfe, BAKING_INGREDIENT, ::BakingIngredient)

    fun cerealProduct(dfe: DataFetchingEnvironment, id: ID) =
        instanceQuery(dfe, CEREAL_PRODUCT, id, ::CerealProduct)

    fun cerealProducts(dfe: DataFetchingEnvironment) =
        collectionQuery(dfe, CEREAL_PRODUCT, ::CerealProduct)

    fun dish(dfe: DataFetchingEnvironment, id: ID) =
        instanceQuery(dfe, DISH, id, ::Dish)

    fun dishes(dfe: DataFetchingEnvironment) =
        collectionQuery(dfe, DISH, ::Dish)

    private fun <T> instanceQuery(dfe: DataFetchingEnvironment, typeUri: String, id: ID, mapper: (Topic) -> T): T? =
        dmx(dfe).getTopic(id.value.toLong())?.let { topic ->
            if (topic.typeUri == typeUri) {
                return mapper(topic)
            } else {
                throw IllegalArgumentException("id is not a $typeUri")
            }
        }

    private fun <T> collectionQuery(dfe: DataFetchingEnvironment, typeUri: String, mapper: (Topic) -> T): List<T> =
        dmx(dfe).getTopicsByType(typeUri).map { topic ->
            mapper(topic)
        }

    private fun dmx(dfe: DataFetchingEnvironment) = dfe.graphQlContext.get<CoreService>("dmx")

    companion object {
        const val BAKING_INGREDIENT = "dmx.cooking.baking_ingredient"
        const val CEREAL_PRODUCT = "dmx.cooking.cereal_product"
        const val DISH = "dmx.cooking.dish"
    }
}

@GraphQLIgnore
abstract class DMXType {
    protected abstract val topic: Topic
    protected val childTopics by lazy { topic.childTopics }

    fun id() = ID(topic.id.toString())
}

data class BakingIngredient(override val topic: Topic) : DMXType() {
    fun name() = topic.simpleValue.toString()
}

data class CerealProduct(override val topic: Topic) : DMXType() {
    fun name() = topic.simpleValue.toString()
}

data class Dish(override val topic: Topic) : DMXType() {
    fun name() = childTopics.getString("dmx.cooking.name_of_dish")
    fun description(): String? = childTopics.getString("dmx.cooking.description_of_dish", null)
    fun bookmark() = topic.childTopics.getTopicOrNull("dmx.bookmarks.bookmark#dmx.cooking.source")?.let { Bookmark(it) }
    fun preparationTime() = runCatching { topic.childTopics.getInt("dmx.cooking.preparation_time") }.getOrNull()
}

data class Bookmark(override val topic: Topic) : DMXType() {
    fun url(): String? = childTopics.getString("dmx.base.url", null)
    fun description(): String? = topic.childTopics.getString("dmx.bookmarks.description", null)
}

/*
                {
                    "childTypeUri": "dmx.cooking.preparation_time",
                    "childCardinalityUri": "dmx.core.one"
                },
                {
                    "childTypeUri": "dmx.cooking.cooking_time",
                    "childCardinalityUri": "dmx.core.one"
                },
                {
                    "childTypeUri": "dmx.cooking.gluten_free",
                    "childCardinalityUri": "dmx.core.one"
                },
                {
                    "childTypeUri": "dmx.cooking.vegetarian",
                    "childCardinalityUri": "dmx.core.one"
                },
                {
                    "childTypeUri": "dmx.cooking.vegan",
                    "childCardinalityUri": "dmx.core.one"
                }
            ],
 */