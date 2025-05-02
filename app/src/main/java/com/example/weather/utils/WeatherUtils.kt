package com.example.weather.utils

import com.example.whether.R

class WeatherUtils {
    fun getWeatherIcon(weatherCode: Int, isDay: Boolean): Int {
        if (isDay) {
            when (weatherCode) {
                0 -> return R.drawable.ic_clear
                1 -> return R.drawable.ic_partly_cloudy
                2 -> return R.drawable.ic_mostly_cloudy
                3 -> return R.drawable.ic_cloudy
                45, 48 -> return R.drawable.ic_fog
                in 51..57 -> return R.drawable.ic_drizzle
                in 61..67 -> return R.drawable.ic_rain
                in 71..77 -> return R.drawable.ic_snow
                in 80..86 -> return R.drawable.ic_rain
                in 95..99 -> return R.drawable.ic_tunderstorm
            }
        } else {
            when (weatherCode) {
                0 -> return R.drawable.ic_clear_night
                1 -> return R.drawable.ic_partly_cloudy_night
                2 -> return R.drawable.ic_mostly_cloudy_night
                3 -> return R.drawable.ic_cloudy
                45, 48 -> return R.drawable.ic_fog
                in 51..57 -> return R.drawable.ic_drizzle
                in 61..67 -> return R.drawable.ic_rain
                in 71..77 -> return R.drawable.ic_snow
                in 80..86 -> return R.drawable.ic_rain
                in 95..99 -> return R.drawable.ic_tunderstorm
            }
        }
        return 0;
    }
}