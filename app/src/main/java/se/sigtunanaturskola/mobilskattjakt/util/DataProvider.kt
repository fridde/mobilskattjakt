package se.sigtunanaturskola.mobilskattjakt.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.*
import se.sigtunanaturskola.mobilskattjakt.R
import se.sigtunanaturskola.mobilskattjakt.activities.log
import se.sigtunanaturskola.mobilskattjakt.data.CoordMap
import se.sigtunanaturskola.mobilskattjakt.data.CoordTagContent


class DataProvider(val activity: Activity) {

    val COORD_WRITER_ACTIVE = activity.getString(R.string.coord_writer_active)
    val COORD_DATA = activity.getString(R.string.coord_data)
    val USER_IS_ADMIN = activity.getString(R.string.user_is_admin)
    val GAME_DATA = activity.getString(R.string.game_data)
    val KEEP_ALIVE = activity.getString(R.string.keep_alive)

    val gson = Gson()
    val appData: SharedPreferences = activity.getSharedPreferences("APP", Context.MODE_PRIVATE)
    var coordDataMap = CoordMap()
    var gameAnimalsLeft = mutableListOf<Int>()
    var gameNextAnimal: Int? = null

    init {
        initCoordMapFomStorage()
    }

    fun setWriterStatus(active: Boolean) {
        appData.edit().putBoolean(COORD_WRITER_ACTIVE, active).apply()
    }

    fun isWriterActive(): Boolean {
        return appData.getBoolean(COORD_WRITER_ACTIVE, false)
    }

    fun setUserIsAdmin(isAdmin: Boolean) {
        appData.edit().putBoolean(USER_IS_ADMIN, isAdmin).apply()
    }

    fun userIsAdmin(): Boolean {
        return appData.getBoolean(USER_IS_ADMIN, false)
    }

    fun userIsPlayer(): Boolean {
        return ! userIsAdmin()
    }

    fun keepAliveActive(): Boolean {
        return appData.getBoolean(KEEP_ALIVE, false)
    }

    fun setKeepAlive(aliveActive: Boolean) {
        appData.edit().putBoolean(KEEP_ALIVE, aliveActive).apply()
    }

    fun initCoordMapFomStorage() {
        val coordFromStorage =
            gson.fromJson(appData.getString(COORD_DATA, "{}"), JsonObject::class.java)
        val coords = CoordMap(coordFromStorage)

        setCoordinatesFromExternalSource(coords)
    }

    fun getCoordinateTagContentAsString(): String {
        val tagContent = CoordTagContent(coordDataMap)

        return gson.toJson(tagContent.asMap())
    }

    fun saveCoordinateFromString(index: Int, value: String) {
        if ((value.length in 1..3) || (value == "0000") || (value == "")) {
            coordDataMap.remove(index)
        } else {
            val thisCoord = ArrayList<Int>()
            thisCoord.add(value.slice(0..1).toInt())
            thisCoord.add(value.slice(2..3).toInt())

            coordDataMap.put(index, thisCoord)
        }
        appData.edit().putString(COORD_DATA, gson.toJson(coordDataMap)).apply()
    }

    fun setCoordinatesFromExternalSource(externalMap: CoordMap) {
        coordDataMap.clear()
        coordDataMap.putAll(externalMap)
    }

    @SuppressLint("ApplySharedPref")
    fun saveCoordinatesFromMap(coordMap: CoordMap) {
        setCoordinatesFromExternalSource(coordMap)
        appData.edit().putString(COORD_DATA, gson.toJson(coordDataMap)).commit()
    }

    @SuppressLint("ApplySharedPref")
    fun clearCoordinatesAndSave() {
        coordDataMap.clear()
        appData.edit().remove(COORD_DATA).commit()
    }

    fun saveGameStatus(game: Game) {
        val status =
            hashMapOf(
                "animals_left" to game.animalsLeft,
                "next_animal" to game.nextAnimal
            )
        appData.edit().putString(GAME_DATA, gson.toJson(status)).apply()

    }

    fun loadGameStatus() {
        val gameStatus = gson.fromJson(appData.getString(GAME_DATA, "{}"), JsonObject::class.java)

        val animalIterator = gameStatus.getAsJsonArray("animals_left")?.iterator()
        animalIterator?.let {
            while (it.hasNext()) {
                gameAnimalsLeft.add(it.next().asInt)
            }
        }
        gameNextAnimal = gameStatus.getAsJsonPrimitive("next_animal")?.asInt
    }

}



