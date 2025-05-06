package com.example.weather.dto

data class HourlyWeather(
    val time: List<String>,
    val temperature_2m: List<Float>,
    val weather_code: List<Int>,
    val is_day: List<Int>,
    val relative_humidity_2m: List<Float>,
    val surface_pressure: List<Float>,
    val visibility: List<Int>,
    val dew_point_2m: List<Float>,
    val uv_index: List<Float>,
    val rain: List<Float>
)
