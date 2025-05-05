package com.example.weather.api

import com.example.weather.dto.astro.AspectsResponse
import com.example.weather.dto.astro.AstroRequest
import com.example.weather.dto.astro.NatalWheelChartResponse
import com.example.weather.dto.astro.PlanetsResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface AstrologyApi {
    @POST("western/natal-wheel-chart")
    suspend fun getNatalWheelChart(
        @Body request: AstroRequest
    ): NatalWheelChartResponse

    @POST("western/planets")
    suspend fun getPlanets(
        @Body request: AstroRequest
    ): PlanetsResponse

    @POST("western/aspects")
    suspend fun getAspects(
        @Body request: AstroRequest
    ): AspectsResponse
}