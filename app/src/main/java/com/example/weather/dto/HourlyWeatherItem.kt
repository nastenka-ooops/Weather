package com.example.weather.dto

data class HourlyWeatherItem(
    val time: String,
    val temperature: Float,
    val weatherCode: Int,
    val isDay: Int
)
