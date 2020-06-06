package systems.dmx.cooking.migrations;

import systems.dmx.core.TopicType;
import systems.dmx.core.model.CompDefModel;
import systems.dmx.core.service.Migration;

public class Migration7 extends Migration {
	@Override
	public void run() {
		/* Add HTML "Annotation" to "Dish".
		In the form generator, list it before the preparation time of the dish. */
		TopicType type = dmx.getTopicType("dmx.cooking.dish");
		type.addCompDefBefore(mf.newCompDefModel(
            "dmx.cooking.dish", "dmx.cooking.annotation", "dmx.core.many"
		), "dmx.cooking.preparation_time");
		// Add "Tag" to "Dish"
		type.addCompDef(mf.newCompDefModel(
			"dmx.cooking.dish", "dmx.tags.tag", "dmx.core.many")
		);
	}
}
