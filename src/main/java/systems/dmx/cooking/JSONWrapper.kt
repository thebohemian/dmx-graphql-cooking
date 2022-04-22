package systems.dmx.cooking

import org.codehaus.jettison.json.JSONObject

class JSONWrapper(val `object`: JSONObject) {
    fun toJSON(): JSONObject {
        return `object`
    }
}
