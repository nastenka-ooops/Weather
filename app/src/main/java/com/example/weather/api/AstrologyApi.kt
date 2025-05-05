package com.example.weather.api

import com.example.weather.dto.astro.AspectsResponse
import com.example.weather.dto.astro.NatalWheelChartResponse
import com.example.weather.dto.astro.PlanetsResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface AstrologyApi {
    @POST("western/natal-wheel-chart")
    suspend fun getNatalWheelChart(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("date") date: Int,
        @Query("hours") hours: Int,
        @Query("minutes") minutes: Int,
        @Query("seconds") seconds: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: Float
    ): NatalWheelChartResponse

    @POST("western/planets")
    suspend fun getPlanets(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("date") date: Int,
        @Query("hours") hours: Int,
        @Query("minutes") minutes: Int,
        @Query("seconds") seconds: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: Float,
        @Query("config[observation_point]") observationPoint: String? = null,
        @Query("config[ayanamsha]") ayanamsha: String? = null,
        @Query("config[language]") language: String? = null
    ): PlanetsResponse

    @POST("western/aspects")
    suspend fun getAspects(
        @Query("year") year: Int,
        @Query("month") month: Int,
        @Query("date") date: Int,
        @Query("hours") hours: Int,
        @Query("minutes") minutes: Int,
        @Query("seconds") seconds: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: Float,
        @Query("config[observation_point]") observationPoint: String? = null,
        @Query("config[ayanamsha]") ayanamsha: String? = null,
        @Query("config[language]") language: String? = null,
        @Query("config[exclude_planets]") excludePlanets: List<String>? = null,
        @Query("config[allowed_aspects]") allowedAspects: List<String>? = null,
        @Query("config[orb_values][Conjunction]") conjunctionOrb: Float? = null,
        @Query("config[orb_values][Opposition]") oppositionOrb: Float? = null,
        @Query("config[orb_values][Square]") squareOrb: Float? = null,
        @Query("config[orb_values][Trine]") trineOrb: Float? = null
    ): AspectsResponse
}