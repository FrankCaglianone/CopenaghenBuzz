package dk.itu.moapd.copenhagenbuzz.fcag

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import android.widget.AutoCompleteTextView


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        createDropdownEventType()
    }


    /**
     *
     * DropDown Item for the Event Type.
     *
     * val eventTypes is the string array of options in res/values/strings.xml "event_types"
     * val arrayAdapter is
     * val autoCompleteTextView is
     *
     */
    private fun createDropdownEventType() {
        val eventTypes = resources.getStringArray(R.array.event_types)
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_item_layout, eventTypes)
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCompleteTextView.setAdapter(arrayAdapter)
    }








}