package se.sigtunanaturskola.mobilskattjakt.util

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import se.sigtunanaturskola.mobilskattjakt.R
import se.sigtunanaturskola.mobilskattjakt.activities.MainActivity

class ScreenManager(val activity: MainActivity) {

    val data = activity.data
    val screenReopener = ScreenReopener(activity)

    private val viewFlags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

    private val winFlags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_FULLSCREEN
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON // keep, we still use API 25
            )

    fun getView(idName: String): Any {

        val id = getResource(idName, "id")
        return activity.findViewById(id)
    }

    fun getTextView(idName: String): TextView {
        return getView(idName) as TextView
    }

    fun getImageView(idName: String): ImageView {
        return getView(idName) as ImageView
    }

    fun setupNextAnimalScreen(animalId: Int?) {
        if (animalId == null) {
            return
        }

        val emojiImageView = getImageView("animal_emoji_svg")
        emojiImageView.setImageDrawable(getEmojiDrawable(animalId))

        val redView = getTextView("red")
        redView.text = data.coordDataMap.getRedCoord(animalId).toString()

        val blueView = getTextView("blue")
        blueView.text = data.coordDataMap.getBlueCoord(animalId).toString()


    }

    fun getEmojiDrawable(animalId: Int): Drawable? {
        val name = "animal_" + animalId.toString().padStart(2, '0')
        return activity.getDrawable(getResource(name,"drawable"))
    }

    fun setBigText(text: String) {
        getTextView("big_text").text = text
    }

    fun setSmallText(text: String) {
        getTextView("small_text").text = text
    }

    @SuppressLint("SetTextI18n")
    fun setRemainingAnimals(nr: Int){
        getTextView("remaining_animal_count").text = "Kvar: ${nr + 1}"
    }

    fun getResource(idName: String, defType: String): Int {
        return activity.resources.getIdentifier(idName, defType, activity.packageName)
    }

    fun goToMainScreen() {
        val intent = Intent(activity, MainActivity::class.java)
        //activity.setTurnScreenOn(true)
        activity.startActivity(intent)
    }

    fun setView(view: String) {
        val id: Int = when (view) {
            "main" -> R.layout.activity_main
            "black" -> R.layout.black_screen
            "go_to_start" -> R.layout.go_to_start_screen
            "you_already_won" -> R.layout.you_already_won_screen
            "winner" -> R.layout.winner_screen
            "next_animal" -> R.layout.next_animal_screen
            else -> R.layout.activity_main
        }

        activity.setContentView(id)
        hideUnnecessaryBars()
    }

    fun hideUnnecessaryBars() {

        activity.window.setFlags(winFlags, winFlags)
        activity.window.decorView.systemUiVisibility = viewFlags
        activity.supportActionBar?.hide()
    }

    fun toast(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun scheduleReopening(){
        screenReopener.startAlarm()
    }

    fun cancelReopening(){
        screenReopener.cancelAlarm()
    }
}