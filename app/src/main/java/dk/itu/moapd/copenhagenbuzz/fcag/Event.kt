package dk.itu.moapd.copenhagenbuzz.fcag

import com.google.android.material.floatingactionbutton.FloatingActionButton

data class Event(val eventName: String,
                 var eventLocation: String,
                 val eventDate: String,
                 val eventType: String,
                 var eventDescription: String,
                 var addEventButton: FloatingActionButton
)