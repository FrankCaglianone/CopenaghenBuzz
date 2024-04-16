package dk.itu.moapd.copenhagenbuzz.fcag.data




/**
 * A data class representing the location of an Event within the Copenhagen Buzz application.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property address The actual address of the location in string format.
 */
data class EventLocation(
    var latitude: Double? = null,
    var longitude: Double? = null,
    var address: String? = null
)

