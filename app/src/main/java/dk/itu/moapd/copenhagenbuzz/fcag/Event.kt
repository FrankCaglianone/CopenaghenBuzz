package dk.itu.moapd.copenhagenbuzz.fcag

import android.widget.Button

data class Event(val eventName: String,
                 var eventLocation: String,
                 val eventDate: String,
                 val eventType: String,
                 var eventDescription: String,
                 var addEventButton: Button)