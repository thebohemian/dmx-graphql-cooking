package systems.dmx.cooking;

import systems.dmx.core.model.AssocModel;
import systems.dmx.core.osgi.PluginActivator;
import systems.dmx.core.service.event.PreCreateAssoc;
import systems.dmx.core.util.DMXUtils;



public class CookingPlugin extends PluginActivator implements PreCreateAssoc {

    @Override
    public void preCreateAssoc(AssocModel assoc) {
        // Associations from instances of the following topic types to instances of "Dish" shall always be "Ingredient amount" associations.
        // Baking ingredient <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.baking_ingredient", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Cereal product <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.cereal_product", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Dairy product <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.dairy_product", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Fish <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.fish", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Fruit product <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.fruit", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Herb <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.herb", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Legume <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.legume", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Liquid <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.liquid", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Meat <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.meat", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Nut <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.nut", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Oil <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.oil", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Poultry <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.poultry", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Sauce <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.sauce", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Soy product <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.soy_product", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Spice <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.spice", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Sweetener <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.sweetener", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        // Vegetable <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.cooking.vegetable", "dmx.cooking.dish",
            "dmx.cooking.ingredient_amount", "dmx.core.default", "dmx.core.default");
        //
        // Associations from instances of "Web address" to instances of "Dish" shall always be "Source" associations.
        //
        // Bookmark <-> Dish
        DMXUtils.associationAutoTyping(assoc, "dmx.bookmarks.bookmark", "dmx.cooking.dish",
            "dmx.cooking.source", "dmx.core.default", "dmx.core.default");
    }
}
