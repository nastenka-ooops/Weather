package com.example.weather.dto

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val relativehumidity_2m: List<Int>,
    val windspeed_10m: List<Float>
)