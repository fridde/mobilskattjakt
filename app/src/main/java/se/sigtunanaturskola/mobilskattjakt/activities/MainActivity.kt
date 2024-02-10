package se.sigtunanaturskola.mobilskattjakt.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import se.sigtunanaturskola.mobilskattjakt.*
import se.sigtunanaturskola.mobilskattjakt.util.*

const val DEBUG = false

fun log(msg: String?) {
    if (DEBUG) Log.i("fridde", msg ?: "null")
}

class MainActivity : AppCompatActivity() {

    lateinit var data: DataProvider
    lateinit var nfcHandler: NFCHandler
    lateinit var screen: ScreenManager
    lateinit var tagReactor: TagReactor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // keep order!
        data = DataProvider(this)
        nfcHandler = NFCHandler(this)
        screen = ScreenManager(this)
        tagReactor = TagReactor(this)


        if (data.userIsAdmin()) {
            screen.setView("main")
            initilizeWriterActiveSwitch()
        } else if (tagReactor.game.isWinner()) {
            screen.setView("winner")
        } else {
            tagReactor.buildAndShowNextAnimalScreen()
        }

    }

    override fun onResume() {
        super.onResume()
        nfcHandler.setupForegroundDispatch()
        screen.cancelReopening()
        screen.hideUnnecessaryBars()
    }

    override fun onPause() {
        super.onPause()
        nfcHandler.disableForegroundDispatch()
    }

    override fun onStop() {
        super.onStop()
        if (data.keepAliveActive()) screen.scheduleReopening()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        tagReactor.handleIntent(intent)
    }

    fun goToEnterCoordView(view: View) {
        val intent = Intent(this, EnterCoordinateActivity::class.java)
        startActivity(intent)
    }

    fun initilizeWriterActiveSwitch() {
        val switch: Switch = findViewById(R.id.coord_writer_switch)
        switch.setOnCheckedChangeListener { _, isChecked ->
            data.setWriterStatus(isChecked)
        }
        switch.isChecked = data.isWriterActive()
    }
}