package com.example.weather.dto

data class DailyWeatherItem(
    val temperatureDay: Float,
    val temperatureNight: Float,
    val time: String,
    val weatherCode: Int
)
