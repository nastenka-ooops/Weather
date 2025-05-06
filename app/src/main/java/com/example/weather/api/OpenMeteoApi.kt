package com.example.weather.api

import com.example.weather.dto.AirQualityResponse
import com.example.weather.dto.LocationResponse
import com.example.weather.dto.LocationsList
import com.example.weather.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") currentWeather: Boolean = true,
        @Query("hourly") hourly: String = "temperature_2m,weather_code,is_day,relative_humidity_2m,surface_pressure,visibility,"+
        "dew_point_2m,precipitation,uv_index,rain",
        @Query("daily") daily: String = "precipitation_sum,sunrise,sunset,uv_index_max,precipitation_probability_mean," +
                "daylight_duration,temperature_2m_max,temperature_2m_min,weather_code,wind_gusts_10m_max",

        @Query("timezone") timezone: String = "auto",
        @Query("wind_speed_unit") windSpeedUnit: String,
        @Query("temperature_unit") temperatureUnit: String
    ): WeatherResponse

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone,aerosol_optical_depth,dust,uv_index,ammonia,european_aqi",
        @Query("timezone") timezone: String = "auto"
    ): AirQualityResponse

    @GET("v1/search")
    suspend fun getLocations(
        @Query("name") name: String
    ): LocationsList
}