package com.example.weather.dto

data class WeatherResponse(
    val current_weather: CurrentWeather,
    val daily: DailyWeather,
    val hourly: HourlyWeather
)
