package com.example.weather.dto.astro

data class PlanetsResponse(
    val statusCode: Int,
    val output: List<PlanetData>
)
