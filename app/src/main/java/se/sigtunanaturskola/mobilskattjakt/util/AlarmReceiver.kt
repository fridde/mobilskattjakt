package se.sigtunanaturskola.mobilskattjakt.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import se.sigtunanaturskola.mobilskattjakt.activities.MainActivity
import se.sigtunanaturskola.mobilskattjakt.activities.log

class AlarmReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        log("Alarm received at "+ System.currentTimeMillis())
        val i = Intent(context, MainActivity::class.java)
        context?.startActivity(i)
    }
}