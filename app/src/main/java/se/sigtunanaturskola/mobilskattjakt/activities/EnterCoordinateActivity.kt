package se.sigtunanaturskola.mobilskattjakt.activities

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import se.sigtunanaturskola.mobilskattjakt.util.DataProvider
import se.sigtunanaturskola.mobilskattjakt.R


class EnterCoordinateActivity : AppCompatActivity() {

    val editIds = HashMap<Int, Int>()
    lateinit var data: DataProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enter_coordinates)

        data = DataProvider(this)

        initializeFields()
    }

    fun initializeFields() {
        val columns: LinearLayout = findViewById(R.id.coord_form)

        var i = 1; // animals start at 1
        columns.children.map { it as LinearLayout }.forEach { column ->
            column.children.map { it as LinearLayout }.forEach { coord_field ->
                coord_field.children.forEach {
                    when (it) {
                        is EditText -> initializeCoordEditTextField(it, i)
                        is TextView -> setLabelText(it, i)
                    }
                }
                i += 1
            }
        }
    }


    fun setLabelText(v: TextView, i: Int) {
        v.text = i.toString()
    }

    fun initializeCoordEditTextField(v: EditText, index: Int) {
        v.id = View.generateViewId()
        editIds.put(index, v.id)
        val potCoord = data.coordDataMap.get(index)?.map{a: Int ->
            a.toString().padStart(2, '0')
        }
        v.setText(potCoord?.joinToString(""))

        v.doOnTextChanged { text, _, _, _ ->
            if (text.toString().length >= 4) {
                val nextView: EditText? = getEditTextField(index + 1)
                nextView?.requestFocus()
            }
        }

        v.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                data.saveCoordinateFromString(index, v.text.toString())
            }
        }
    }

    fun getEditTextField(index: Int): EditText? {
        return editIds.get(index)?.let {
            findViewById(it)
        }
    }

    fun clearCoordinateFields(v: View) {
        data.clearCoordinatesAndSave()
        Thread.sleep(200L)
        finish()
        startActivity(intent)
    }
}
