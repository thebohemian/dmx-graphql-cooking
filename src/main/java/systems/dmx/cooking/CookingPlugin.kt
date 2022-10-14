// This file implements autotyping:
// Both association types provided by this plugin shall be automatically used when instances of certain topic types are linked to an instance of dish.
package systems.dmx.cooking

import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.server.execution.GraphQLServer
import com.expediagroup.graphql.server.types.GraphQLResponse
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.GraphQLSchema
import kotlinx.coroutines.runBlocking
import org.codehaus.jettison.json.JSONObject
import systems.dmx.cooking.graphql.*
import systems.dmx.core.model.AssocModel
import systems.dmx.core.osgi.PluginActivator
import systems.dmx.core.service.Inject
import systems.dmx.core.service.event.PreCreateAssoc
import systems.dmx.core.util.DMXUtils
import systems.dmx.workspaces.WorkspacesService
import java.net.URI
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path(CookingPlugin.BACKEND_PREFIX)
class CookingPlugin : PluginActivator(), PreCreateAssoc {

    @Inject
    private lateinit var wss: WorkspacesService

    private lateinit var schema: GraphQLSchema

    private lateinit var server: GraphQLServer<JSONObject>
    private lateinit var dmxServer: GraphQLServer<JSONObject>

    private val objectMapper = ObjectMapper()

    override fun init() {
        //schema = generateDMXSchema(dmx)
        schema = generateSchema()
        server = GraphQLServer(
            requestParser = CookingRequestParser(objectMapper),
            requestHandler = createRequestHandler(schema),
            contextFactory = CookingContextFactory(dmx, wss))

        val dmxSchema = generateDMXSchema(dmx)
        dmxServer = GraphQLServer(
            requestParser = CookingRequestParser(objectMapper),
            requestHandler = createRequestHandler(dmxSchema),
            contextFactory = CookingContextFactory(dmx, wss))
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/graphql")
    fun executeGraphQL(body: JSONWrapper): String {
        // Execute the query against the schema
        return runBlocking {
            server.execute(body.toJSON())?.let { result ->
                objectMapper.writeValueAsString(result)
            } ?: throw WebApplicationException(Response.Status.BAD_REQUEST)
        }
    }

    @GET
    @Path("/sdl")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    fun getGraphQLSchema(): String {
        return schema.print()
    }

    @GET
    @Path("/graphiql")
    fun getGraphiQL(): Response {
        return Response.temporaryRedirect(URI("/com.github.thebohemian.dmx-graphql-cooking/graphiql-playground.html")).build()
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/dmx-graphql")
    fun executeDmxGraphQL(body: JSONWrapper): String {
        // Execute the query against the schema
        return runBlocking {
            dmxServer.execute(body.toJSON())?.let { result ->
                objectMapper.writeValueAsString(result)
            } ?: throw WebApplicationException(Response.Status.BAD_REQUEST)
        }
    }

    @GET
    @Path("/dmx-graphiql")
    fun getDmxGraphiQL(): Response {
        return Response.temporaryRedirect(URI("/com.github.thebohemian.dmx-graphql-cooking/dmx-graphiql-playground.html")).build()
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