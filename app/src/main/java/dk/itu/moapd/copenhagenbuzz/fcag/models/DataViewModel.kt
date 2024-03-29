package dk.itu.moapd.copenhagenbuzz.fcag.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataViewModel : ViewModel() {

    // Define a LiveData object to hold a list of Event objects.
    // Use MutableLiveData to allow for updates to the data.
    private val _events = MutableLiveData<List<Event>>()
    private val _favorites = MutableLiveData<List<Event>>()


    // Public LiveData, exposed for observation
    // -- Events
    val events: LiveData<List<Event>> = _events
    init {
        fetchEventsAsync()
    }
    // -- Favorites
    val favorites: LiveData<List<Event>> = _favorites
    init {
        fetchFavoritesAsync()
    }






    // Call this method to fetch events asynchronously
    private fun fetchEventsAsync() {
        viewModelScope.launch {
            val events = generateEvents()
            // Update the private MutableLiveData which in turn updates the exposed LiveData
            _events.value = events
        }
    }



    private fun fetchFavoritesAsync() {
        viewModelScope.launch {
            val favorites = generateFavorites()
            _favorites.value = favorites
        }
    }



    // Simulates event generation in a non-blocking way
    private suspend fun generateEvents(): List<Event> = withContext(Dispatchers.Default) {
        val events = mutableListOf<Event>()
        for (i in 0 until 10) {
            val event = Event(
                eventName = "Event Name #$i",
                eventLocation = "Event Location #$i",
                eventDate = "Event Date #$i",
                eventType = "Event Type #$i",
                eventDescription = "Event Description #$i"
            )
            events.add(event)
        }
        events // Return the generated list of events
    }



    private suspend fun generateFavorites(): List<Event> = withContext(Dispatchers.Default) {
        val favorites = mutableListOf<Event>()
        for (i in 0 until 10) {
            val event = Event(
                eventName = "Event Name #$i",
                eventLocation = "Event Location #$i",
                eventDate = "Event Date #$i",
                eventType = "Event Type #$i",
                eventDescription = "Event Description #$i"
            )
            favorites.add(event)
        }
        favorites
    }



} // end of class