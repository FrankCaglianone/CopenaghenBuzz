package dk.itu.moapd.copenhagenbuzz.fcag.locationServices

import android.util.Log
import dk.itu.moapd.copenhagenbuzz.fcag.data.EventLocation
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class Geocoding {

    companion object {
        var TAG = "Geocoding"
    }



    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val apiKey = dotenv["GEOCODING_API_KEY"]



    suspend fun getLocationCoordinates(location: String): EventLocation {
        val baseUrl = "https://geocode.maps.co/search"
        val encodedLocation = URLEncoder.encode(location, "UTF-8")
        val urlString = "$baseUrl?q=$encodedLocation&api_key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("Content-type", "application/json")
                connection.setRequestProperty("Accept", "application/json")
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val reader = InputStreamReader(connection.inputStream)
                val response = StringBuilder()
                val bufferedReader = BufferedReader(reader)

                bufferedReader.useLines { lines ->
                    lines.forEach { line ->
                        response.append(line.trim())
                    }
                }

                val jsonArray = JSONArray(response.toString())

                if (jsonArray.length() > 0) {
                    val firstLocation = jsonArray.getJSONObject(0)
                    val latitude = firstLocation.optString("lat")
                    val longitude = firstLocation.optString("lon")
                    Log.d(TAG, "IT WORKS lat:$latitude long:$longitude")
                    EventLocation(latitude.toDouble(), longitude.toDouble(), location)
                } else {
                    EventLocation(null, null, "No location found")
                }
            } catch (e: Exception) {
                EventLocation(null, null, "Error: ${e.message}")
            }
        }
    }


}