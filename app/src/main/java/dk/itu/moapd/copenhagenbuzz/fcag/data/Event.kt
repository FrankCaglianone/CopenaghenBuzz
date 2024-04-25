package dk.itu.moapd.copenhagenbuzz.fcag.data

import java.util.UUID


/**
 * A data class representing an event within the Copenhagen Buzz application.
 *
 * This class encapsulates the properties of an event, including its name, location, date,
 * type, and description.
 *
 * @property eventName The name of the event.
 * @property eventLocation The location where the event is to be held.
 * @property eventDate The date on which the event is scheduled, in a string format.
 * @property eventType The type of the event.
 * @property eventDescription A detailed description of what the event is about.
 * @property eventId The unique identifier for the event
 * @property userId The unique identifier for the user
 * @property eventPhotoUrl The URL of the photo stored in the Firebase Storage
 */
data class Event(var eventName: String? = null,
                 var eventLocation: EventLocation? = null,
                 var eventDate: String? = null,
                 var eventType: String? = null,
                 var eventDescription: String? = null,
                 var eventId: String? = null,
                 var userId: String? = null,
                 var eventPhotoUrl: String? = null,
)