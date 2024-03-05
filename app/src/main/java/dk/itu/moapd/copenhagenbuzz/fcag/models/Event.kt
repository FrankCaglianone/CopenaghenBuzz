package dk.itu.moapd.copenhagenbuzz.fcag.models




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
 */
data class Event(var eventName: String,
                 var eventLocation: String,
                 var eventDate: String,
                 var eventType: String,
                 var eventDescription: String,
)