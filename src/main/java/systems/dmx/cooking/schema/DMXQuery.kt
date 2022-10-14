package systems.dmx.cooking.schema

import graphql.schema.DataFetchingEnvironment
import systems.dmx.core.CompDef
import systems.dmx.core.Topic
import systems.dmx.core.TopicType
import systems.dmx.core.service.CoreService
import systems.dmx.core.DMXType as CoreDMXType

class DMXQuery {

    fun topicTypes(dfe: DataFetchingEnvironment) = dmx(dfe).allTopicTypes.map {
        DMXTopicType(it)
    }

    fun topicType(dfe: DataFetchingEnvironment, topicTypeUri: String) = DMXTopicType(dmx(dfe).getTopicType(topicTypeUri))

    fun topicsByType(dfe: DataFetchingEnvironment, topicTypeUri: String) = dmx(dfe).getTopicsByType(topicTypeUri).map(::DMXTopic)

    private fun dmx(dfe: DataFetchingEnvironment) = dfe.graphQlContext.get<CoreService>("dmx")
}

class DMXTopicType(private val type: CoreDMXType) {
    fun id() = type.id.toString()
    fun uri() = type.uri
    fun type() = DMXTopicType(type.type)
    fun typeUri() = type.typeUri
    fun compDefs() = type.compDefs.map(::CompositionDefinition)
    fun datatypeUri() = type.dataTypeUri
}

class CompositionDefinition(private val compDef: CompDef) {
    fun id() = compDef.id.toString()
    fun typeUri() = compDef.typeUri
    fun type() = DMXTopicType(compDef.type)
    fun uri() = compDef.uri
    fun childCardinalityUri() = compDef.childCardinalityUri
    fun parentTypeUri() = compDef.parentTypeUri
    fun childTypeUri() = compDef.childTypeUri
    fun customAssocTypeUri(): String? = compDef.customAssocTypeUri
    fun instanceLevelTypeUri() = compDef.instanceLevelAssocTypeUri
    fun compDefUri() = compDef.compDefUri
}

class DMXTopic(private val topic: Topic) {
    fun id() = topic.id.toString()
    fun uri() = topic.uri
    fun simpleValue() = topic.simpleValue.toString()
}