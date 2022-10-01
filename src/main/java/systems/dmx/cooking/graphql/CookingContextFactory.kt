package systems.dmx.cooking.graphql

import com.expediagroup.graphql.server.execution.GraphQLContextFactory
import com.expediagroup.graphql.generator.execution.GraphQLContext
import org.codehaus.jettison.json.JSONObject
import systems.dmx.core.service.CoreService
import systems.dmx.workspaces.WorkspacesService

class CookingContextFactory(
    private val dmx: CoreService,
    private val wss: WorkspacesService
) : GraphQLContextFactory<GraphQLContext, JSONObject> {

    override suspend fun generateContextMap(request: JSONObject): Map<*, Any> = mapOf(
        "dmx" to dmx,
        "wss" to wss
    )

}
