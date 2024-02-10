package se.sigtunanaturskola.mobilskattjakt.util

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import se.sigtunanaturskola.mobilskattjakt.activities.log

const val RENEWAL_INTERVAL = 5_000L

class ScreenReopener(val activity: Activity) {

    val alarmManager = activity.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val pendingIntent: PendingIntent

    init {
        val intent = Intent(activity, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }

    fun startAlarm(){
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + RENEWAL_INTERVAL,
            pendingIntent
        )
        //log("Alarm scheduled")
    }

    fun cancelAlarm(){
        //log("cancelled")
        alarmManager?.cancel(pendingIntent)
    }
}