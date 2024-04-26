package dk.itu.moapd.copenhagenbuzz.fcag.locationServices

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import android.Manifest


class DefaultLocationClient (private val context: Context, private val client: FusedLocationProviderClient): LocationClient {




    /**
     * This method checks if the user allows the application uses all location-aware resources to
     * monitor the user's location.
     *
     * @return A boolean value with the user permission agreement.
     */
    private fun Context.hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }







    /**
     * Provides real-time location updates as a Kotlin Flow. This method sets up and manages
     * location updates based on the given interval. It checks for required location and GPS
     * permissions and ensures that the GPS or network providers are enabled. If any conditions are
     * not met, it throws a LocationClient.LocationException.
     * It continuously receives location updates at the specified interval until the coroutine that
     * collects the Flow is cancelled or until the Flow collector is disposed of using `awaitClose`.
     *
     * Implementation Details:
     * - The method subscribes to location updates from the Android LocationManager via a specified LocationRequest.
     * - It adjusts the update interval to the specified value and sets the same value as the fastest interval.
     * - The method properly handles cancellation of the Flow via `awaitClose`, which unsubscribes
     *   from the location updates to conserve resources.
     *
     * @param interval The time interval in milliseconds between successive location updates.
     * @return A Flow emitting Location objects whenever a new location is fetched.
     * @throws LocationClient.LocationException if location permissions are missing or if both GPS
     *                                          and network providers are disabled.
     */
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            // Check for location permissions
            if(!context.hasLocationPermissions()) {
                throw LocationClient.LocationException("Missing location permissions")
            }

            // Check for GPS permissions
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("Gps is disabled")
            }

            // Create the request
            val request = LocationRequest.create()
                .setInterval(interval)
                .setFastestInterval(interval)


            // Create the callback
            val locationCallback = object: LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    // Get the last element of the locations list (the newly fetched location)
                    result.locations.lastOrNull()?.let { location->
                        launch { send(location) }
                    }
                }
            }


            // Get the location
            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())


            // Stop getting the location
            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }

        }
    }


}