package dk.itu.moapd.copenhagenbuzz.fcag

import android.app.Application
import android.util.Log
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.material.color.DynamicColors

class MyApplication : Application(), OnMapsSdkInitializedCallback {

    companion object {
        var TAG = "MyApplication"
    }

    override fun onCreate() {
        super.onCreate()
        // Apply dynamic colors to activities if available.
        DynamicColors.applyToActivitiesIfAvailable(this)

        // Initialize google maps
        MapsInitializer.initialize(this, MapsInitializer.Renderer.LATEST, this);
    }


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