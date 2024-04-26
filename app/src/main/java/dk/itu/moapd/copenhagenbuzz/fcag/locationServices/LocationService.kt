package dk.itu.moapd.copenhagenbuzz.fcag.locationServices


import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.LocationServices
import dk.itu.moapd.copenhagenbuzz.fcag.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    // A set of private variables used in this class.
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient


    // A set of private constants used in this class.
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        const val LOCATION_UPDATE_ACTION = "com.example.broadcast.LOCATION_UPDATE"
    }




    /**
     * This method is a binder callback that returns `null` because this service is not meant to be
     * bound to any components.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null as binding is not supported by this service.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }




    /**
     * Initializes the service by creating and initializing the location client.
     * This method is called when the service is being created for the first time, before it starts
     * handling intents that start or stop location updates.
     */
    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }





    /**
     * Handles the start requests for the service, responding to intents with specific actions.
     * The service handles two actions:
     *      - ACTION_START to start location tracking
     *      - ACTION_STOP to stop the service.
     *
     * @param intent The Intent supplied to `startService(Intent)`, containing the action to be performed.
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start.
     * @return The return value indicates what semantics the system should use for the service's current started state.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }






    /**
     * Starts the location tracking process by posting an ongoing notification and subscribing to
     * location updates. The location is updated in the notification and broadcasted to the MapsFragment
     * through a custom action defined by LOCATION_UPDATE_ACTION.
     */
    private fun start() {
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location....")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)


        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lat = location.latitude
                val long = location.longitude

                // Send broadcast with the location data
                val intent = Intent(LOCATION_UPDATE_ACTION)
                intent.putExtra("latitude", lat)
                intent.putExtra("longitude", long)
                sendBroadcast(intent)


                val updateNotification = notification.setContentText(
                    "Location: ($lat, $long)"
                )
                notificationManager.notify(1, updateNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }






    /**
     * Stops the service by stopping the foreground notification and self-stopping the service.
     * This method is intended to stop all ongoing operations and clean up resources when location
     * updates are no longer needed.
     */
    private fun stop() {
        stopForeground(true)
        stopSelf()
    }





    /**
     * Cleans up resources when the service is destroyed. This includes cancelling any ongoing
     * operations or coroutines associated with this service. It ensures that the service does not
     * leave any tasks running in the background when it is no longer needed.
     */
    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }




}