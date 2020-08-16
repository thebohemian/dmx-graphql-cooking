package systems.dmx.cooking.migrations;

import systems.dmx.core.TopicType;
import systems.dmx.core.model.CompDefModel;
import systems.dmx.core.DMXObject;
import systems.dmx.core.service.Migration;

public class Migration8 extends Migration {
	@Override
	public void run() {
		//
		TopicType type = dmx.getTopicType("dmx.cooking.dish");
		/* Add "Book" from biblio plugin as a possible source to the "Dish" composite.
		In the form generator, list it before the annotation.*/
		type.addCompDefBefore(mf.newCompDefModel(
			"dmx.cooking.source", false, false, "dmx.cooking.dish", "dmx.biblio.monograph", "dmx.core.one"
			), "dmx.cooking.annotation");
	}
}
