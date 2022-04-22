package systems.dmx.cooking.graphql

import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import org.codehaus.jettison.json.JSONObject
import systems.dmx.core.service.CoreService

class CookingContextFactory(
        private val dmx: CoreService
) : GraphQLContextFactory<CookingContext, JSONObject> {
    override suspend fun generateContext(request: JSONObject) = CookingContext(dmx)
}
