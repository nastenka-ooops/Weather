package com.example.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whether.R
import com.example.weather.dto.LocationResponse
import com.example.weather.utils.WeatherUtils

class SavedLocationsAdapter(
    private val onLocationClick: (LocationResponse) -> Unit,
    private val onDeleteClick: (LocationResponse) -> Unit
) : RecyclerView.Adapter<SavedLocationsAdapter.ViewHolder>() {

    private val locations = mutableListOf<LocationResponse>()
    private val weatherUtils = WeatherUtils()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocationName: TextView = itemView.findViewById(R.id.tv_location_name)
        val tvTemperature: TextView = itemView.findViewById(R.id.tv_temperature)
        val ivWeatherIcon: ImageButton = itemView.findViewById(R.id.iv_weather_icon)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]

        holder.tvLocationName.text = location.name
        holder.tvTemperature.text = "${location.temperature}°"

        if (location.weatherCode != null && location.isDay != null) {
            holder.ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(location.weatherCode, location.isDay == 1)
            )
        }

        holder.itemView.setOnClickListener {
            onLocationClick(location)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(location)
        }
    }

    override fun getItemCount(): Int = locations.size

    fun setLocationsList(newLocations: List<LocationResponse>) {
        locations.clear()
        locations.addAll(newLocations)
        notifyDataSetChanged()
    }
}