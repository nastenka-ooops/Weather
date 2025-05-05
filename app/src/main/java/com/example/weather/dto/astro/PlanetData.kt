package com.example.weather.dto.astro

data class PlanetData(
    val planet: LocalizedName,
    val fullDegree: Double,
    val normDegree: Double,
    val isRetro: String,
    val zodiac_sign: ZodiacSign
)
