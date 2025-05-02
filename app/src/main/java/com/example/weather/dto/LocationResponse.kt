package com.example.weather.dto

import java.io.Serializable

class LocationResponse(
    val latitude: Double,
    val longitude: Double,
    val name: String,
    val country: String,
    var temperature: Float,
    var weatherCode: Int,
    var isDay: Int
): Serializable