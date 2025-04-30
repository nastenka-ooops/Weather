package com.example.weather.dto


data class CurrentWeather(
    val temperature: Float,
    val windspeed: Float,
    val weathercode: Int,
    val time: String
)
