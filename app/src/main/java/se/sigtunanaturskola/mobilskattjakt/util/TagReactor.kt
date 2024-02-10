package se.sigtunanaturskola.mobilskattjakt.util

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.nfc.NfcAdapter
import se.sigtunanaturskola.mobilskattjakt.R
import se.sigtunanaturskola.mobilskattjakt.activities.MainActivity
import se.sigtunanaturskola.mobilskattjakt.activities.log
import se.sigtunanaturskola.mobilskattjakt.data.*


class TagReactor(val activity: MainActivity) {

    val nfcHandler = activity.nfcHandler
    val data = activity.data
    val screen = activity.screen
    val game = Game(data).loadGameData()

    fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action) {
            reactToTag(intent)
        }
    }

    fun reactToTag(intent: Intent) {
        if (data.isWriterActive()) {
            val coordContent = data.getCoordinateTagContentAsString()
            nfcHandler.writeStringToTagByIntent(coordContent, intent)
            screen.goToMainScreen()
            return
        }
        val msg = nfcHandler.getNdefMessageFromIntent(intent)
        val tagContent = GenericTagContent(msg)

        when (tagContent.type) {
            "coord" -> saveCoordinatesFromTag(tagContent.toCoordTagContent())
            "animal" -> reactToAnimalTag(tagContent.toAnimalTagContent())
            "start" -> reactToStartTag()
            "you_are_admin" -> reactToAdminTag(tagContent.toAdminTagContent())
            "lock" -> reactToLockTag()
            else -> screen.toast("Unknown tag of type \"${tagContent.type}\"")
        }
    }


    fun saveCoordinatesFromTag(tag: CoordTagContent) {

        setMaxVolume()
        data.saveCoordinatesFromMap(tag.coords)
        game.initialize()
        screen.setView("go_to_start")
    }

    fun reactToAnimalTag(tag: AnimalTagContent) {

        if (game.isWinner()) {
            screen.setView("you_already_won")
            return
        }

        if (game.isNextAnimal(tag.animalIndex)) {
            game.approveAnimal()
            if (game.isWinner()) {
                screen.setView("winner")
                return
            } else {
                // Well done! Next animal!
                buildAndShowNextAnimalScreen()
                return
            }
        } else {
            // Wrong animal!
            screen.setView("next_animal")
            screen.setupNextAnimalScreen(game.nextAnimal)
            screen.setBigText(activity.getString(R.string.facepalm))
            screen.setSmallText("Fel djur! Gå till")
            screen.setRemainingAnimals(game.animalsLeft.size)
            return
        }

    }

    fun reactToStartTag() {
        if (game.isWinner()) {
            screen.setView("you_already_won")
            return
        }
        screen.setView("next_animal")
        screen.setupNextAnimalScreen(game.nextAnimal)
        screen.setBigText(activity.getString(R.string.crayon))
        screen.setSmallText("Första djur:")
    }

    fun reactToAdminTag(tag: AdminTagContent) {
        if(tag.isAdmin){
            makeAdmin()
        } else {
            makeUser()
        }
        screen.goToMainScreen()
    }

    fun makeAdmin(){
        data.setUserIsAdmin(true)
        unlock()
        screen.toast("You are now ADMIN")
    }
    fun makeUser(){
        data.setUserIsAdmin(false)
        screen.toast("You are now USER")
    }

    fun lock(){
        data.setKeepAlive(true)
        activity.startLockTask()
        screen.toast("LOCKED")
    }

    fun unlock(){
        data.setKeepAlive(false)
        activity.stopLockTask()
        screen.cancelReopening()
        screen.toast("UNLOCKED")
    }

    fun reactToLockTag() {
        if(data.userIsAdmin() || data.keepAliveActive()){
            return unlock()
        }
        return lock()
    }

    fun buildAndShowNextAnimalScreen() {
        screen.setView("next_animal")
        screen.setupNextAnimalScreen(game.nextAnimal)
        screen.setBigText(activity.getString(R.string.thumb_up))
        screen.setSmallText("Nästa djur:")
        screen.setRemainingAnimals(game.animalsLeft.size)
    }

    fun setMaxVolume() {
        val aM = activity.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = aM.getStreamMaxVolume(AudioManager.STREAM_SYSTEM)
        aM.setStreamVolume(AudioManager.STREAM_SYSTEM, maxVolume, AudioManager.FLAG_PLAY_SOUND)
    }

}