package com.example.weather.dto

data class DailyWeather(
    val sunrise: List<String>,
    val sunset: List<String>,
    val uv_index_max: List<Float>,
    val precipitation_probability_mean: List<Int>,
    val daylight_duration: List<Float>,
    val temperature_2m_max: List<Float>,
    val temperature_2m_min: List<Float>,
    val weather_code: List<Int>,
    val time: List<String>
)
