package systems.dmx.cooking.graphql

import com.expediagroup.graphql.generator.execution.GraphQLContext
import systems.dmx.core.service.CoreService

data class CookingContext(
        val dmx: CoreService
) : GraphQLContext
