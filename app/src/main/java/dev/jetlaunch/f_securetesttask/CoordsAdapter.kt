package dev.jetlaunch.f_securetesttask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.jetlaunch.locationtracker.entity.LocationData
import java.text.SimpleDateFormat
import java.util.*

class CoordsAdapter: RecyclerView.Adapter<CoordsAdapter.CoordsVH>() {
    init {
        setHasStableIds(true)
    }

    var items = listOf<LocationData>()
    set(value) {
        field = value
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoordsVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.coords_layout, parent, false)
        return CoordsVH(view)
    }

    override fun getItemCount(): Int {
       return items.size
    }

    override fun onBindViewHolder(holder: CoordsVH, position: Int) {
        items[position].let { item ->
            val date = Date()
                date.time = item.timeStamp
            val timestamp = SimpleDateFormat("kk:mm:ss", Locale.getDefault()).format(date)
            holder.time.text = timestamp
            holder.coords.text = "Lat: ${item.lat}, Long: ${item.lon}"
        }
    }

    override fun getItemId(position: Int): Long {
        return return items[position].timeStamp
    }

    class CoordsVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_timestamp)
        val coords: TextView = itemView.findViewById(R.id.tv_coords)
    }
}