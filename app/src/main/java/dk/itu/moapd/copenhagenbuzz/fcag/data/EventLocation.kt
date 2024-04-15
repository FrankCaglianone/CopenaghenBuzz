package dk.itu.moapd.copenhagenbuzz.fcag.data




/**
 * A data class representing the location of an Event within the Copenhagen Buzz application.
 *
 * @property latitude The latitude of the location.
 * @property longitude The longitude of the location.
 * @property address The actual address of the location in string format.
 */
data class EventLocation(
    val latitude: Double,
    val longitude: Double,
    val address: String
)

