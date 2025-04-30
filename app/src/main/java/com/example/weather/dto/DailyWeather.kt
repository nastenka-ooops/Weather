package com.example.weather.dto

data class DailyWeather(
    val sunrise: List<String>,
    val sunset: List<String>,
    val uv_index_max: List<Float>,
    val precipitation_probability_mean: List<Int>,
    var daylight_duration: List<Float>
)
