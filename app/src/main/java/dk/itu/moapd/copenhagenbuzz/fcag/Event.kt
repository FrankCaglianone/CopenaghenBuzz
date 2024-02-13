package dk.itu.moapd.copenhagenbuzz.fcag

data class Event(val eventName: String,
                 var eventLocation: String,
                 val startDate: Long,
                 val endDate: Long,
                 val eventType: String,
                 var eventDescription: String)