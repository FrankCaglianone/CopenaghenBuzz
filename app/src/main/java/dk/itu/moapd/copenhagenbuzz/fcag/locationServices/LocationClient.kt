package dk.itu.moapd.copenhagenbuzz.fcag.locationServices


import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {


    /**
     * Provides a Flow of Location objects, each representing the user's current location at
     * specified intervals. This method allows for real-time location tracking.
     *
     * @param interval The desired interval between location updates in milliseconds.
     * @return A Flow stream of Location objects, enabling the consumer to handle location data reactively.
     * @throws LocationException If there are issues setting up the location updates.
     */
    fun getLocationUpdates(interval: Long): Flow<Location>



    /**
     * Exception to be thrown when there are problems retrieving location updates.
     * @param message Detailed message describing the error condition.
     */
    class LocationException(message: String): Exception()
}