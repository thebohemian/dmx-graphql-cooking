package systems.dmx.cooking.graphql

import com.expediagroup.graphql.server.execution.GraphQLRequestParser
import com.expediagroup.graphql.server.types.GraphQLRequest
import org.codehaus.jettison.json.JSONObject

class CookingContextParser : GraphQLRequestParser<JSONObject> {

    override suspend fun parseRequest(request: JSONObject) = GraphQLRequest(
            query = request.getString("query"),
            operationName = request.optString("operationName"),
            variables = request.optJSONObject("variables")?.toMap()
    )

    @Suppress("unchecked_cast")
    private fun JSONObject.toMap(): Map<String, String> =
            (keys() as Iterator<String>).asSequence().associateWith { optString(it) }

}
