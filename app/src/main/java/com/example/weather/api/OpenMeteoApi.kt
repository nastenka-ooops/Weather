package com.example.weather.api

import com.example.weather.dto.AirQualityResponse
import com.example.weather.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        //@Query("hourly") hourly: String = "temperature_2m,relativehumidity_2m,windspeed_10m",
        @Query("daily") daily: String = "sunrise,sunset,uv_index_max,precipitation_probability_mean,daylight_duration",
        @Query("timezone") timezone: String = "auto",
        @Query("wind_speed_unit") windSpeedUnit: String = "ms"
    ): WeatherResponse

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("timezone") timezone: String = "auto",
        @Query("current") current: Boolean = true
    ): AirQualityResponse
}