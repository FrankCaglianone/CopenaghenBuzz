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

    // A set of private constants used in this class.
    companion object {
        var TAG = "Geocoding"
    }


    // Environment Variables
    private val dotenv = dotenv {
        directory = "/assets"
        filename = "env"
    }
    private val apiKey = dotenv["GEOCODING_API_KEY"]




    /**
     * Retrieves geographic coordinates for a given location string by querying an external
     * geocoding API. This function sends an HTTP GET request to the geocoding service, processes
     * the JSON response, and returns an `EventLocation` object containing the address, latitude and
     * longitude of the queried location.
     *
     * @param location The location name (e.g., "New York") to be geocoded.
     * @return An `EventLocation` object containing the address, latitude and longitude of the
     *         location. If the geocoding fails or no location is found, the latitude and
     *         longitude fields in the returned `EventLocation` will be null, and the address field
     *         will contain an error message or "No location found".
     */
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