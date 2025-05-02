package com.example.weather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.LocationResponse
import com.example.weather.utils.WeatherUtils
import com.example.whether.R
import com.example.whether.databinding.SearchItemLayoutBinding

class SearchLocationsAdapter
    (private val onLocationClick: (LocationResponse) -> Unit) :
    RecyclerView.Adapter<SearchLocationsAdapter.SearchHolder>() {
    private val locationList = ArrayList<LocationResponse>()

    class SearchHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = SearchItemLayoutBinding.bind(view)
        private var weatherUtils: WeatherUtils = WeatherUtils()

        @SuppressLint("SetTextI18n")
        fun bind(location: LocationResponse) = with(binding) {
            tvCityName.text = "${location.name}, ${location.country}"
            tvTemperature.text = "${location.temperature}°"
            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(
                    location.weatherCode,
                    location.isDay == 1
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_item_layout, parent, false)
        return SearchHolder(view)
    }

    override fun getItemCount(): Int {
        return locationList.size
    }


    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val recipe = locationList[position]
        holder.bind(recipe)
        holder.itemView.setOnClickListener {
            onLocationClick(recipe)
        }
    }

    fun setLocationsList(list: List<LocationResponse>) {
        locationList.clear()
        locationList.addAll(list)
        notifyDataSetChanged()
    }
}