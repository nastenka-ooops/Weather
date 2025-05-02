package com.example.weather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.HourlyWeatherItem
import com.example.weather.utils.WeatherUtils
import com.example.whether.R
import com.example.whether.databinding.HourlyWeatherItemLayoutBinding

class HourlyWeatherAdapter : RecyclerView.Adapter<HourlyWeatherAdapter.WeatherHolder>() {
    private val hourlyWeatherList = ArrayList<HourlyWeatherItem>()

    class WeatherHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = HourlyWeatherItemLayoutBinding.bind(view)
        private var weatherUtils: WeatherUtils = WeatherUtils()

        @SuppressLint("SetTextI18n")
        fun bind(hourlyWeather: HourlyWeatherItem) = with(binding) {

            val currentTime = hourlyWeather.time.substring(11, 16)
            tvTime.text = currentTime

            tvTemperature.text = "${hourlyWeather.temperature}°"
            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(
                    hourlyWeather.weatherCode,
                    hourlyWeather.isDay == 1
                )
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.hourly_weather_item_layout, parent, false)
        return WeatherHolder(view)
    }

    override fun getItemCount(): Int {
        return hourlyWeatherList.size
    }


    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        holder.bind(hourlyWeatherList[position])
    }

    fun setLocationsList(list: List<HourlyWeatherItem>) {
        hourlyWeatherList.clear()
        hourlyWeatherList.addAll(list)
        notifyDataSetChanged()
    }
}