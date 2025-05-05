package com.example.weather.dto.astro

data class AstroRequest(
    val year: Int,
    val month: Int,
    val date: Int,
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val latitude: Double,
    val longitude: Double,
    val timezone: Float,
    val config: Map<String, Any> = HashMap()
)