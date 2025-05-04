package com.example.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.whether.R
import com.example.weather.dto.LocationResponse
import com.example.weather.utils.SharedPreferencesHelper
import com.example.weather.utils.WeatherUtils

class SavedLocationsAdapter(
    private val onLocationClick: (LocationResponse) -> Unit,
    private val onSetDefaultClick: (LocationResponse) -> Unit,
    private val onDeleteClick: (LocationResponse) -> Unit,
    private val sharedPreferencesHelper: SharedPreferencesHelper

) : RecyclerView.Adapter<SavedLocationsAdapter.ViewHolder>() {

    private val locations = mutableListOf<LocationResponse>()
    private val weatherUtils = WeatherUtils()
    private var selectedLocation: LocationResponse? = null


    fun setSelectedLocation(location: LocationResponse?) {
        val previous = selectedLocation
        selectedLocation = location
        previous?.let { notifyItemChanged(locations.indexOf(it)) }
        location?.let { notifyItemChanged(locations.indexOf(it)) }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvLocationName: TextView = itemView.findViewById(R.id.tv_location_name)
        val tvTemperature: TextView = itemView.findViewById(R.id.tv_temperature)
        val ivWeatherIcon: ImageButton = itemView.findViewById(R.id.iv_weather_icon)
        val btnSetDefault: Button = itemView.findViewById(R.id.btn_set_default)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
        val tvIsDefault: TextView = itemView.findViewById(R.id.tv_is_default)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_location, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = locations[position]
        val isDefault = sharedPreferencesHelper.getSelectedLocation()?.name == location.name
        holder.tvLocationName.text = location.name
        holder.tvTemperature.text = "${location.temperature}°"

        if (location.weatherCode != null && location.isDay != null) {
            holder.ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(location.weatherCode, location.isDay == 1)
            )
        }
        holder.ivWeatherIcon.visibility = View.VISIBLE
        holder.btnDelete.visibility = View.VISIBLE
        // Проверяем, является ли эта локация основной
        if (isDefault) {
            holder.btnSetDefault.visibility = View.GONE
            holder.tvIsDefault.visibility = View.VISIBLE
        } else {
            holder.btnSetDefault.visibility = View.VISIBLE
            holder.tvIsDefault.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onLocationClick(location)
        }

        holder.btnSetDefault.setOnClickListener {
            onSetDefaultClick(location)
            // Обновляем отображение после изменения основной локации
            notifyDataSetChanged()
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