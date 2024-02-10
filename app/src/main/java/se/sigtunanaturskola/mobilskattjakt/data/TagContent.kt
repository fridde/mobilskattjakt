package se.sigtunanaturskola.mobilskattjakt.data

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import se.sigtunanaturskola.mobilskattjakt.activities.log


class CoordTagContent(var coords: CoordMap){

    fun asMap(): HashMap<String, CoordMap> {
        return hashMapOf("coord" to coords)
    }
}

class AnimalTagContent(val animalIndex: Int = -1)

class AdminTagContent(val isAdmin: Boolean)

class GenericTagContent(msg: String = "{}"){
    val obj: JsonObject = Gson().fromJson(msg, JsonObject::class.java)
    var type: String = obj.entrySet().first().key.toString()
    var data: JsonElement = obj.get(type)


    fun toCoordTagContent(): CoordTagContent {
        return CoordTagContent(CoordMap(data.asJsonObject))
    }

    fun toAnimalTagContent(): AnimalTagContent{
        return AnimalTagContent(data.asInt)
    }

    fun toAdminTagContent(): AdminTagContent{
        val isAdmin = data.asNumber.toInt() != 0
        return AdminTagContent(isAdmin)
    }
}