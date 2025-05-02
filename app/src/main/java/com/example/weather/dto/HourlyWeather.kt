package com.example.weather.dto

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val weather_code: List<Int>,
    val is_day: List<Int>
)