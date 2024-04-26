package dk.itu.moapd.copenhagenbuzz.fcag

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.material.color.DynamicColors

class MyApplication : Application(), OnMapsSdkInitializedCallback {

    // A set of private constants used in this class.
    companion object {
        var TAG = "MyApplication"
    }


    override fun onCreate() {
        super.onCreate()

        // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Initialize google maps
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this);


        // Creates a notification channel for location updates.
        val channel = NotificationChannel("location", "location", NotificationManager.IMPORTANCE_LOW)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }




    /**
     * Callback method triggered when the Google Maps SDK initialization process completes.
     * This method logs which version of the Maps renderer is used—either the latest or the legacy.
     * Implementing this method allows the application to respond accordingly based on the renderer
     * version that is initialized.
     *
     * @param renderer The renderer version of the Google Maps SDK that was initialized.
     *                 This can either be `MapsInitializer.Renderer.LATEST` or `MapsInitializer.Renderer.LEGACY`.
     */
    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> {
                Log.d(TAG, "The latest version of the renderer is used.")
            }
            MapsInitializer.Renderer.LEGACY -> {
                Log.d(TAG, "The legacy version of the renderer is used.")
            }
        }
    }

}