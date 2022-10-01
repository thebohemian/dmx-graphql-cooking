package systems.dmx.cooking.graphql

import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.types.GraphQLRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.codehaus.jettison.json.JSONObject

class CookingRequestParser(
    private val objectMapper: ObjectMapper
) : GraphQLRequestParser<JSONObject> {

    override suspend fun parseRequest(request: JSONObject) = GraphQLRequest(
            query = request.getString("query"),
            operationName = request.optString("operationName"),
            variables = request.optJSONObject("variables")?.toMap()
    ).also {
        println(it)
    }

    @Suppress("unchecked_cast")
    private fun JSONObject.toMap(): Map<String, String> =
            (keys() as Iterator<String>).asSequence().associateWith { optString(it) }

}
