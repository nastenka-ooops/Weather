package com.example.weather.utils

object AirQualityUtils {
    fun getAqiDescription(aqi: Int?): String {
        return when (aqi) {
            null -> "No data"
            in 0..20 -> "Air quality is Good"
            in 21..40 -> "Air quality is Fair"
            in 41..60 -> "Air quality is Moderate"
            in 61..80 -> "Air quality is Poor"
            in 81..100 -> "Air quality is Very poor"
            else -> "Air quality is Extremely poor"
        }
    }
}