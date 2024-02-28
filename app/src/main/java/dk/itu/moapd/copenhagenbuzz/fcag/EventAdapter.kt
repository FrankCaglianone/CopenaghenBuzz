package dk.itu.moapd.copenhagenbuzz.fcag

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import dk.itu.moapd.copenhagenbuzz.fcag.databinding.EventRowItemBinding

class EventAdapter(private val context: Context, private val events: List<Event>) : BaseAdapter() {

    class ViewHolder(val binding: EventRowItemBinding) {}


    override fun getCount(): Int { return events.size }
    override fun getItem(position: Int): Any { return events[position] }
    override fun getItemId(position: Int): Long { return position.toLong() }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

}