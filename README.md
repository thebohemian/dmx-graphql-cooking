# Data Model and Autotyping for the DMX cooking show case

This plugin implements all topic types and association types needed for the cooking showcase used on the [DMX Demo Server](https://demo.dmx.systems) and in the webcast and in the [DMX Modeling Tutorial](https://vimeo.com/393512831)" (though slightly different here). It also provides autotyping for associations related to dish instances.

## Download

DMX Cooking:

https://download.dmx.systems/plugins/dmx-cooking/

DMX Cooking (CI):

https://download.dmx.systems/ci/dmx-cooking/dmx-cooking-latest.jar

## Dependency

DMX Cooking depends on the DMX Biblio plugin:

DMX Biblio:

https://download.dmx.systems/plugins/dmx-biblio/ (Upcoming...)

DMX Biblio (CI):

https://download.dmx.systems/ci/dmx-biblio/dmx-biblio-latest.jar (Upcoming...)

## Usage

Download the .jar file of dmx-biblio and dmx-cooking to the bunde-deploy folder of your DMX instance (also see the section on [Plugin Installation](https://dmx.readthedocs.io/en/latest/admin.html#plugin-installation) in the DMX Admin Documentation).

In DMX create an instance of the topic type "Dish", search for ingredients and create associations between them and the dish. The associations are automatically assigned the association type "Ingredient amount". Edit the associations to specify the quantities.

## Licensing

DMX Cooking is available freely under the GNU Affero General Public License, version 3.
All third party components incorporated into the DMX Cooking Software are licensed under the original license provided by the owner of the applicable component.

## Credits

Based on Jörg Richter's [DMX Plugin Template](https://git.dmx.systems/dmx-plugins/dmx-plugin-template).

## Version History

**0.1.1** -- Jun xx, 2020

* dependency to dmx-biblio plugin
* Books are now supported sources
* Adapted for DMX 5.0-SNAPSHOT

**0.1** -- Apr 03, 2020

* first release
* Requires DMX 5.0-beta-7

