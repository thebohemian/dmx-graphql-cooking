package systems.dmx.cooking.graphql

import graphql.Scalars.*
import graphql.scalars.ExtendedScalars.GraphQLLong
import graphql.schema.*
import systems.dmx.core.CompDef
import systems.dmx.core.Topic
import systems.dmx.core.TopicType
import systems.dmx.core.model.SimpleValue
import systems.dmx.core.service.CoreService
import java.util.logging.Logger
import systems.dmx.core.Constants as DmxCoreConstants

const val QUERY_TYPE_NAME = "Query"

data class DmxFieldResolver<T>(
    val fieldCoordinates: FieldCoordinates,
    val dataFetcher: DataFetcher<T>
) {
    companion object {
        fun newTopicsResolver(typeUri: String, topicObjectTypeName: String) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(QUERY_TYPE_NAME, "${topicObjectTypeName}_list"),
                dataFetcher = {
                    (it.graphQlContext.get("dmx") as CoreService).getTopicsByType(typeUri)
                }
            )

        /** Resolver for a whole topic */
        fun newTopicResolver(topicObjectTypeName: String) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(QUERY_TYPE_NAME, topicObjectTypeName),
                dataFetcher = {
                    val id = (it.arguments["id"] as String).toLong()
                    (it.graphQlContext.get("dmx") as CoreService).getTopic(id)
                }
            )
        fun newCompDefResolver(parentTypeName: String, fieldName: String, compDef: CompDef) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(parentTypeName, fieldName),
                dataFetcher = {
                    // TODO: List result
                    it.getSource<Topic>().childTopics.getTopicOrNull(compDef.compDefUri)?.let {
                        when (it.type.dataTypeUri) {
                            DmxCoreConstants.TEXT, DmxCoreConstants.HTML -> it.simpleValue.toString()
                            DmxCoreConstants.BOOLEAN -> it.simpleValue.booleanValue()
                            DmxCoreConstants.NUMBER -> it.simpleValue
                            DmxCoreConstants.ENTITY, DmxCoreConstants.COMPOSITE, DmxCoreConstants.VALUE -> it
                            else -> null
                        }
                    }
                }
            )

        /** Resolver for the id field */
        fun newIdFieldResolver(objectTypeName: String) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "id"),
                dataFetcher = { (it.getSource() as Topic).id.toString() }
            )

        fun newUriFieldResolver(objectTypeName: String) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "uri"),
                dataFetcher = { (it.getSource() as Topic).uri }
            )

        fun newTypeUriFieldResolver(objectTypeName: String) =
            DmxFieldResolver<Any?>(
                fieldCoordinates = FieldCoordinates.coordinates(objectTypeName, "typeUri"),
                dataFetcher = { (it.getSource() as Topic).typeUri }
            )

        fun newValueFieldResolver(parentObjectTypeName: String, fieldDataTypeUri: String, knownTypes: Map<String, GraphQLOutputType>) =
            DmxFieldResolver(
                fieldCoordinates = FieldCoordinates.coordinates(parentObjectTypeName, "value"),
                dataFetcher = {
                    val topic = (it.getSource() as Topic)
                    when (fieldDataTypeUri) {
                        DmxCoreConstants.TEXT, DmxCoreConstants.HTML -> topic.simpleValue.toString()
                        DmxCoreConstants.BOOLEAN -> topic.simpleValue.booleanValue()
                        DmxCoreConstants.NUMBER -> topic.simpleValue
                        DmxCoreConstants.ENTITY, DmxCoreConstants.COMPOSITE, DmxCoreConstants.VALUE -> topic
                        else -> null
                    }
                }
            )
    }
}

data class TypeAndResolvers(
    val typeUri: String,
    val objectType: GraphQLObjectType,
    val fieldResolvers: (types : Map<String, GraphQLOutputType>) -> List<DmxFieldResolver<Any?>>
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

object DmxTopic {
    val idFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("id")
        .type(GraphQLNonNull.nonNull(GraphQLID))
        .build()

    val uriFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("uri")
        .type(GraphQLNonNull.nonNull(GraphQLString))
        .build()

    val typeUriFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("typeUri")
        .type(GraphQLNonNull.nonNull(GraphQLString))
        .build()

    fun valueFieldDefinition(topicType: TopicType) = GraphQLFieldDefinition.newFieldDefinition()
        .name("value")
        .type(toGraphQLType(topicType))
        .build()

    private fun canHaveValueField(topicType: TopicType) =
        topicType.dataTypeUri in listOf(DmxCoreConstants.BOOLEAN, DmxCoreConstants.NUMBER, DmxCoreConstants.TEXT, DmxCoreConstants.HTML)
    
    private fun objectTypeName(typeUri: String) =
        typeUri.replace(".", "_")

    private fun childFieldDefinition(fieldName: String, type: GraphQLOutputType) = GraphQLFieldDefinition.newFieldDefinition()
        .name(fieldName)
        .type(type)
        .build()

    private fun fieldName(compDef: CompDef) =
        compDef.compDefUri.replace(".", "_").replace("#", "_")
    fun schemaType(knownTypes: Map<String, GraphQLOutputType>, topicType: TopicType): TypeAndResolvers {
        val typeUri = topicType.uri
        val typeName = objectTypeName(typeUri)

        return TypeAndResolvers(
            typeUri = typeUri,
            objectType = GraphQLObjectType.newObject()
                .name(typeName)
                .field(idFieldDefinition)
                .field(uriFieldDefinition)
                .field(typeUriFieldDefinition)
                .apply {
                    // Value
                    if (canHaveValueField(topicType)) {
                        field(valueFieldDefinition(topicType))
                    }

                    for (compDef in topicType.compDefs) {
                        val fieldName = fieldName(compDef)
                        knownTypes.get(compDef.childTypeUri)?.let {
                            // If it has a value field, use its value otherwise the whole (complex) type
                            val targetType = (it as? GraphQLObjectType)?.getField("value")?.type ?: it

                            field(childFieldDefinition(fieldName, targetType))
                        } ?: println("Can't handle type ${compDef.childTypeUri} because it is not known.")
                    }
                }
                .build(),
            fieldResolvers = { types ->
                buildList {
                    // Resolvers needed for Query for this topic type
                    add(DmxFieldResolver.newTopicResolver(typeName))
                    add(DmxFieldResolver.newTopicsResolver(typeUri, typeName))

                    // fields: id, uri, typeUri
                    add(DmxFieldResolver.newIdFieldResolver(typeName))
                    add(DmxFieldResolver.newUriFieldResolver(typeName))
                    add(DmxFieldResolver.newTypeUriFieldResolver(typeName))

                    // value
                    if (canHaveValueField(topicType)) {
                        add(DmxFieldResolver.newValueFieldResolver(typeName, topicType.dataTypeUri, types))
                    }

                    // children
                    for (compDef in topicType.compDefs) {
                        val fieldName = fieldName(compDef)
                        add(DmxFieldResolver.newCompDefResolver(typeName, fieldName, compDef))
                    }

                }
            }
        )  
    } 
}

object DmxSimpleValue {
    private val dmxNumberTypeName = DmxCoreConstants.NUMBER.replace(".", "_")

    private val longFieldDefinition: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("long")
        .type(GraphQLLong)
        .build()

    private val intFieldDefinition: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("int")
        .type(GraphQLInt)
        .build()

    private val doubleFieldDefinition: GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("double")
        .type(GraphQLFloat)
        .build()

    val objecType = GraphQLObjectType.newObject()
        .name(dmxNumberTypeName)
        .field(intFieldDefinition)
        .field(doubleFieldDefinition)
        .build()

    val schemaType = TypeAndResolvers(
        typeUri = DmxCoreConstants.NUMBER,
        objectType = objecType,
        fieldResolvers = { types ->
            listOf(
                DmxFieldResolver(
                    fieldCoordinates = FieldCoordinates.coordinates(dmxNumberTypeName, intFieldDefinition.name),
                    dataFetcher = { runCatching { (it.getSource() as SimpleValue).intValue() }.getOrNull() }
                ),
                DmxFieldResolver(
                    fieldCoordinates = FieldCoordinates.coordinates(dmxNumberTypeName, doubleFieldDefinition.name),
                    dataFetcher = { runCatching { (it.getSource() as SimpleValue).doubleValue() }.getOrNull() }
                ),
                DmxFieldResolver(
                    fieldCoordinates = FieldCoordinates.coordinates(dmxNumberTypeName, longFieldDefinition.name),
                    dataFetcher = { runCatching { (it.getSource() as SimpleValue).longValue() }.getOrNull() }
                )
            )
        }
    )

}


private fun toGraphQLType(topicType: TopicType): GraphQLOutputType =
    when (topicType.dataTypeUri) {
        DmxCoreConstants.TEXT, DmxCoreConstants.HTML -> GraphQLString
        DmxCoreConstants.BOOLEAN -> GraphQLBoolean
        DmxCoreConstants.NUMBER -> DmxSimpleValue.objecType
        // Unknown types
        else -> GraphQLString
    }

fun generateDMXSchema(dmx: CoreService): GraphQLSchema {
    val knownTypes = mutableMapOf<String, GraphQLOutputType>()
    val topicTypeSchemaTypes = dmx.allTopicTypes.map { topicType ->
        DmxTopic.schemaType(knownTypes, topicType).also {
            knownTypes.put(it.typeUri, it.objectType)
        }
    }

    // Gives instance and list query for all topic types
    val queryType = GraphQLObjectType.newObject()
        .name(QUERY_TYPE_NAME).apply {
            for (schemaType in topicTypeSchemaTypes) {
                field(newInstanceFieldDefinition(schemaType.objectType))
                field(newInstancesFieldDefinition(schemaType.objectType))
            }
        }
        .build()

    // Adds types that can be children
    val schemaTypes = topicTypeSchemaTypes +
            DmxSimpleValue.schemaType

    val codeRegistry: GraphQLCodeRegistry = GraphQLCodeRegistry.newCodeRegistry().apply {
        for (schemaType in schemaTypes) {
            for (fieldResolver in schemaType.fieldResolvers(knownTypes)) {
                dataFetcher(fieldResolver.fieldCoordinates, fieldResolver.dataFetcher)
            }
        }
    }.build()

    return GraphQLSchema.newSchema()
        .query(queryType)
        .codeRegistry(codeRegistry)
        .build()
}