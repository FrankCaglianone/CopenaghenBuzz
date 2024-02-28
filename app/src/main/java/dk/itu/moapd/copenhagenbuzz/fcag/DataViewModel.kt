package dk.itu.moapd.copenhagenbuzz.fcag

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.processNextEventInCurrentThread
import kotlinx.coroutines.withContext

class DataViewModel : ViewModel() {

    // Define a LiveData object to hold a list of Event objects.
    // Use MutableLiveData to allow for updates to the data.
    private val _events = MutableLiveData<List<Event>>()


    // Public LiveData, exposed for observation
    val events: LiveData<List<Event>> = _events



    // Call this method to fetch events asynchronously
    fun fetchEventsAsync() {
        viewModelScope.launch {
            val events = generateEvents()
            // Update the private MutableLiveData which in turn updates the exposed LiveData
            _events.value = events
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


} // end of class