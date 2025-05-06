package com.example.weather.dto

data class WeatherResponse(
    val current_weather: CurrentWeather,
    val daily: DailyWeather,
    val hourly: HourlyWeather,
    val current_weather_units: CurrentWeatherUnits,
    val hourly_units: HourlyWeatherUnits
)
