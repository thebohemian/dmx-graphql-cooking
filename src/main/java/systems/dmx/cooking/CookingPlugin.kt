// This file implements autotyping:
// Both association types provided by this plugin shall be automatically used when instances of certain topic types are linked to an instance of dish.
package systems.dmx.cooking

import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLResponse
import createRequestHandler
import kotlinx.coroutines.runBlocking
import systems.dmx.cooking.graphql.CookingContextFactory
import systems.dmx.cooking.graphql.CookingContextParser
import systems.dmx.core.model.AssocModel
import systems.dmx.core.osgi.PluginActivator
import systems.dmx.core.service.event.PreCreateAssoc
import systems.dmx.core.util.DMXUtils
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

@Path(CookingPlugin.BACKEND_PREFIX)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class CookingPlugin : PluginActivator(), PreCreateAssoc {

    private val server = GraphQLServer(
            requestParser = CookingContextParser(),
            requestHandler = createRequestHandler(),
            contextFactory = CookingContextFactory(dmx))

    @POST
    @Path("/graphql")
    fun execute(body: JSONWrapper): Any? {
        val response = runBlocking {
            server.execute(body.toJSON())
        }
        return (response as? GraphQLResponse<*>)?.data
    }

    override fun preCreateAssoc(assoc: AssocModel) {
        // Associations from instances of the topic types listed below to instances of "Dish" shall always be "Ingredient amount" associations.
        // Baking ingredient <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.baking_ingredient", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Cereal product <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.cereal_product", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Dairy product <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.dairy_product", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Fish <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.fish", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Fruit product <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.fruit", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Herb <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.herb", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Legume <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.legume", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Liquid <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.liquid", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Meat <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.meat", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Nut <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.nut", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Oil <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.oil", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Poultry <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.poultry", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Sauce <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.sauce", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Soy product <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.soy_product", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Spice <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.spice", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Sweetener <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.sweetener", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Vegetable <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.cooking.vegetable", "dmx.cooking.dish",
                "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default")
        // Associations from instances of "Bookmark" to instances of "Dish" shall always be Custom Association Types "Source".
        // Bookmark <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.bookmarks.bookmark", "dmx.cooking.dish",
                "dmx.cooking.source", "dmx.core.default", "dmx.core.default")
        // Associations from instances of "Book" to instances of "Dish" shall always be Custom Association Types "Source".
        // Book <-> Dish
        DMXUtils.assocAutoTyping(assoc, "dmx.biblio.monograph", "dmx.cooking.dish",
                "dmx.cooking.source", "dmx.core.default", "dmx.core.default")
    }

    companion object {
        const val BACKEND_PREFIX = "cooking-backend"
    }
}