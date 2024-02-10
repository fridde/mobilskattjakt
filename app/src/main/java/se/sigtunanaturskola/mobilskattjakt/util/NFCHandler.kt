package se.sigtunanaturskola.mobilskattjakt.util

import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.widget.Toast
import java.util.*

class NFCHandler(val activity: Activity) {

    val adapter: NfcAdapter = NfcAdapter.getDefaultAdapter(activity)
    lateinit var msgToWrite: NdefMessage

    fun writeStringToTagByIntent(msgString: String, intent: Intent) {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        msgToWrite = createNdefMessage(msgString)
        if (writeMessageToTag(msgToWrite, tag!!)) {
            Toast.makeText(activity, msgString, Toast.LENGTH_LONG).show()
        }
    }


    fun writeMessageToTag(nfcMessage: NdefMessage, tag: Tag): Boolean {
        val ndefTag = Ndef.get(tag)
        ndefTag.connect()
        ndefTag.writeNdefMessage(nfcMessage)
        ndefTag.close()
        return true
    }

    fun createNdefMessage(msg: String): NdefMessage {
        val nfcRecord = NdefRecord.createTextRecord(Locale.ENGLISH.toString(), msg)
        return NdefMessage(nfcRecord)
    }

    fun getNdefMessageFromIntent(intent: Intent): String {
        var result = ""

        intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)?.also { rawMessages ->
            val messages: List<NdefMessage> = rawMessages.map { it as NdefMessage }
            val payload = messages[0].records[0].payload
            result += String(payload.sliceArray(3 until payload.size), Charsets.UTF_8)
        }
        return result
    }

    fun setupForegroundDispatch() {
        val intent = Intent(activity, activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent: PendingIntent = PendingIntent.getActivity(activity, 0, intent, 0)

        val filter = IntentFilter()
        filter.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
        filter.addCategory(Intent.CATEGORY_DEFAULT)
        filter.addDataType("text/plain")
        val filters = arrayOf(filter)

        adapter.enableForegroundDispatch(activity, pendingIntent, filters, arrayOf(arrayOf()))
    }

    fun disableForegroundDispatch() {
        adapter.disableForegroundDispatch(activity)
    }

}