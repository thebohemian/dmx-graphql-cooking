package systems.dmx.cooking.migrations

import systems.dmx.core.service.Migration

class Migration8 : Migration() {
    override fun run() {
        //
        val type = dmx.getTopicType("dmx.cooking.dish")
        /* Add "Book" from biblio plugin as a possible source to the "Dish" composite.
		In the form generator, list it before the annotation.*/type.addCompDefBefore(mf.newCompDefModel(
                "dmx.cooking.source", false, false, "dmx.cooking.dish", "dmx.biblio.monograph", "dmx.core.one"
        ), "dmx.cooking.annotation")
    }
}