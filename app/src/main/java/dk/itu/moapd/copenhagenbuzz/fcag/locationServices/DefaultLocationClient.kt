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


class DefaultLocationClient (
    private val context: Context,
    private val client: FusedLocationProviderClient
): LocationClient {



    private fun Context.hasLocationPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }





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