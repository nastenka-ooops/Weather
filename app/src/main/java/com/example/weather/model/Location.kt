package com.example.weather.model

import android.health.connect.datatypes.units.Temperature

data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String
)
