package com.example.weather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.DailyWeatherItem
import com.example.weather.utils.WeatherUtils
import com.example.whether.R
import com.example.whether.databinding.DailyWeatherItemLayoutBinding

class DailyWeatherAdapter: RecyclerView.Adapter<DailyWeatherAdapter.WeatherHolder>() {
    private val dailyWeatherList = ArrayList<DailyWeatherItem>()

    class WeatherHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = DailyWeatherItemLayoutBinding.bind(view)
        private var weatherUtils: WeatherUtils = WeatherUtils()

        @SuppressLint("SetTextI18n")
        fun bind(dayliWeather: DailyWeatherItem) = with(binding) {

            val currentDay = dayliWeather.time.substring(8)
            val currentMonth = dayliWeather.time.substring(5, 7)

            tvTime.text = "$currentDay $currentMonth"

            tvDayTemperature.text = "${dayliWeather.temperatureDay}°"
            tvNightTemperature.text = "${dayliWeather.temperatureNight}°"
            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(dayliWeather.weatherCode,true)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_weather_item_layout, parent, false)
        return WeatherHolder(view)
    }

    override fun getItemCount(): Int {
        return dailyWeatherList.size
    }


    override fun onBindViewHolder(holder: WeatherHolder, position: Int) {
        holder.bind(dailyWeatherList[position])
    }

    fun setWeatherList(list: List<DailyWeatherItem>) {
        dailyWeatherList.clear()
        dailyWeatherList.addAll(list)
        notifyDataSetChanged()
    }
}