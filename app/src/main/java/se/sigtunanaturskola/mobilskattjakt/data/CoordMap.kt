package se.sigtunanaturskola.mobilskattjakt.data

import com.google.gson.JsonObject

const val RED = 0
const val BLUE = 1

class CoordMap(obj: JsonObject = JsonObject()) : HashMap<Int, ArrayList<Int>>() {

    init {
        this.clear()

        obj.entrySet().forEach {
            val singleCoordArray = ArrayList<Int>()
            val iterator = it.value.asJsonArray.iterator()
            while (iterator.hasNext()) {
                singleCoordArray.add(iterator.next().asInt)
            }
            this.put(it.key.toInt(), singleCoordArray)
        }
    }

    fun getRedCoord(index: Int): Int?{
        return get(index)?.get(RED)
    }

    fun getBlueCoord(index: Int): Int?{
        return get(index)?.get(BLUE)
    }

}
