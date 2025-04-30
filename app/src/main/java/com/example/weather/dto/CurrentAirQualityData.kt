package com.example.weather.dto

data class CurrentAirQualityData(
    val time: String,
    val interval: Int,
    val pm10: Float,
    val pm2_5: Float,
    val carbon_monoxide: Float,
    val nitrogen_dioxide: Float,
    val sulphur_dioxide: Float,
    val ozone: Float,
    val aerosol_optical_depth: Float,
    val dust: Float,
    val uv_index: Float,
    val ammonia: Float,
    val european_aqi: Int?
)
