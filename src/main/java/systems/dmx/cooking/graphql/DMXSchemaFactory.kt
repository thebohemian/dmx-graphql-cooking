package systems.dmx.cooking.graphql

import graphql.Scalars.*
import graphql.schema.*
import systems.dmx.core.Topic
import systems.dmx.core.TopicType
import systems.dmx.core.service.CoreService

const val QUERY_TYPE_NAME = "Query"

private data class DMXFieldResolver<T>(
    val fieldCoordinates: FieldCoordinates,
    val dataFetcher: DataFetcher<T>
) {
    companion object {
        fun newTopicsResolver(typeUri: String, topicObjectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(QUERY_TYPE_NAME, "${topicObjectTypeName}_list"),
                dataFetcher = {
                    (it.graphQlContext.get("dmx") as CoreService).getTopicsByType(typeUri)
                }
            )

        /** Resolver for a whole topic */
        fun newTopicResolver(topicObjectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(QUERY_TYPE_NAME, topicObjectTypeName),
                dataFetcher = {
                    val id = (it.arguments["id"] as String).toLong()
                    (it.graphQlContext.get("dmx") as CoreService).getTopic(id)
                }
            )

        /** Resolver for the id field */
        fun newIdFieldResolver(objectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "id"),
                dataFetcher = { (it.getSource() as Topic).id.toString() }
            )

        fun newUriFieldResolver(objectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "uri"),
                dataFetcher = { (it.getSource() as Topic).uri }
            )

        fun newTypeUriFieldResolver(objectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "typeUri"),
                dataFetcher = { (it.getSource() as Topic).typeUri }
            )

        fun newValueFieldResolver(datatypeUri: String, objectTypeName: String) =
            DMXFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "value"),
                dataFetcher = {
                    val topic = (it.getSource() as Topic)
                    when (datatypeUri) {
                        "dmx.core.text" -> topic.simpleValue.toString()
                        "dmx.core.boolean" -> topic.simpleValue.booleanValue()
                        else -> null
                    }
                }
            )
    }
}

private data class DMXSchemaType(
    val objectType: GraphQLObjectType,
    val fieldResolvers: List<DMXFieldResolver<Any?>>
)

private fun newInstanceFieldDefinition(
    topicObjectType: GraphQLObjectType
): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name(topicObjectType.name)
    .type(topicObjectType)
    .argument(
        GraphQLArgument.newArgument()
            .name("id")
            .type(GraphQLString)
            .build()
    )
    .build()

private fun newInstancesFieldDefinition(
    topicObjectType: GraphQLObjectType
): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("${topicObjectType.name}_list")
    .type(GraphQLList.list(topicObjectType))
    .build()

private val idFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("id")
    .type(GraphQLID)
    .build()

private val uriFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("uri")
    .type(GraphQLString)
    .build()

private val typeUriFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
    .name("typeUri")
    .type(GraphQLString)
    .build()

private fun valueFieldDefinition(topicType: TopicType) = GraphQLFieldDefinition.newFieldDefinition()
    .name("value")
    .type(toGraphQLType(topicType))
    .build()

private fun toGraphQLType(topicType: TopicType) =
    when (topicType.dataTypeUri) {
        "dmx.core.text" -> GraphQLString
        "dmx.core.boolean" -> GraphQLBoolean
        else -> GraphQLString
    }

fun generateDMXSchema(dmx: CoreService): GraphQLSchema {
    val typeUris = listOf("dmx.cooking.baking_ingredient", "dmx.cooking.vegan")

    val schemaTypes = typeUris.map { typeUri ->
        val topicType = dmx.getTopicType(typeUri)
        val topicObjectTypeName = typeUri.replace(".", "_")

        DMXSchemaType(
            objectType = GraphQLObjectType.newObject()
                .name(topicObjectTypeName)
                .field(idFieldDefinition)
                .field(uriFieldDefinition)
                .field(typeUriFieldDefinition)
                .field(valueFieldDefinition(topicType))
                .build(),
            fieldResolvers = listOf(
                // Actual topic resolver for Query
                DMXFieldResolver.newTopicResolver(topicObjectTypeName),
                DMXFieldResolver.newTopicsResolver(typeUri, topicObjectTypeName),

                // id, uri, typeUri
                DMXFieldResolver.newIdFieldResolver(topicObjectTypeName),
                DMXFieldResolver.newUriFieldResolver(topicObjectTypeName),
                DMXFieldResolver.newTypeUriFieldResolver(topicObjectTypeName),

                // value
                DMXFieldResolver.newValueFieldResolver(topicType.dataTypeUri, topicObjectTypeName)
            )
        )

    }

    val queryType = GraphQLObjectType.newObject()
        .name(QUERY_TYPE_NAME).apply {
            for (schemaType in schemaTypes) {
                field(newInstanceFieldDefinition(schemaType.objectType))
                field(newInstancesFieldDefinition(schemaType.objectType))
            }
        }
        .build()

    val codeRegistry: GraphQLCodeRegistry = GraphQLCodeRegistry.newCodeRegistry().apply {
        for (schemaType in schemaTypes) {
            for (fieldResolver in schemaType.fieldResolvers) {
                dataFetcher(fieldResolver.fieldCoordinates, fieldResolver.dataFetcher)
            }
        }
    }.build()

    return GraphQLSchema.newSchema()
        .query(queryType)
        .codeRegistry(codeRegistry)
        .build()
}