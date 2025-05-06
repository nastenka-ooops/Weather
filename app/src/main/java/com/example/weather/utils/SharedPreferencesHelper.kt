package com.example.weather.utils

import LocationWeatherData
import android.content.Context
import android.content.SharedPreferences
import com.example.weather.dto.AirQualityResponse
import com.example.weather.dto.LocationResponse
import com.example.weather.dto.WeatherResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val LOCATIONS_KEY = "saved_locations"
    private val SELECTED_LOCATION_KEY = "selected_location"
    private val WEATHER_DATA_KEY = "weather_data_"
    private val AIR_DATA_KEY = "air_data_"
    private val sharedflag = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    private val CACHE_DURATION = 2 * 60 * 60 * 1000

    fun saveSelectedLocation(location: LocationResponse) {
        val json = gson.toJson(location)
        sharedPreferences.edit().putString(SELECTED_LOCATION_KEY, json).apply()
        setFlag(false);
    }



    fun getSelectedLocation(): LocationResponse? {
        val json = sharedPreferences.getString(SELECTED_LOCATION_KEY, null)
        return if (json != null) {
            gson.fromJson(json, LocationResponse::class.java)
        } else {
            null
        }
    }

    fun saveWeatherData(location: LocationResponse, weather: WeatherResponse, airQuality: AirQualityResponse) {
        val data = mapOf(
            "weather" to gson.toJson(weather),
            "lastUpdated" to System.currentTimeMillis(),
            "air" to gson.toJson(airQuality)
        )
        val json = gson.toJson(data)
        sharedPreferences.edit()
            .putString("$WEATHER_DATA_KEY${location.name}", json)
            .apply()
    }

    // Получаем сохраненные погодные данные для локации
    fun getWeatherData(location: LocationResponse): WeatherResponse? {
        val json = sharedPreferences.getString("$WEATHER_DATA_KEY${location.name}", null) ?: return null
        val data = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type) as? Map<String, Any>

        // Проверяем, не устарели ли данные
        val lastUpdated = data?.get("lastUpdated") as? Long ?: return null
        if (System.currentTimeMillis() - lastUpdated > CACHE_DURATION) return null

        val weatherJson = data["weather"] as? String ?: return null
        return gson.fromJson(weatherJson, WeatherResponse::class.java)
    }

    fun getAirQualityData(location: LocationResponse): AirQualityResponse? {
        val json = sharedPreferences.getString("$AIR_DATA_KEY${location.name}", null) ?: return null
        val data = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type) as? Map<String, Any>

        // Проверяем, не устарели ли данные
        val lastUpdated = data?.get("lastUpdated") as? Long ?: return null
        if (System.currentTimeMillis() - lastUpdated > CACHE_DURATION) return null

        val airJson = data["air"] as? String ?: return null
        return gson.fromJson(airJson, AirQualityResponse::class.java)
    }

    fun isSavedLockation(location: LocationResponse): Boolean {
        val savedLocations = getSavedLocations().toMutableList();
        if(savedLocations.any {it.name == location.name})
        {
            return true;
        }
        else
            return false;
    }


    fun clearSelectedLocation() {
        sharedPreferences.edit().remove(SELECTED_LOCATION_KEY).apply()

        setFlag(true)
    }

    fun setSelectedNull(){
       setFlag(true);
    }

    fun saveLocation(location: LocationResponse) {
        val savedLocations = getSavedLocations().toMutableList()

        // Проверяем, нет ли уже такой локации в списке
        if (!savedLocations.any { it.name == location.name }) {
            savedLocations.add(location)
            val json = gson.toJson(savedLocations)
            sharedPreferences.edit().putString(LOCATIONS_KEY, json).apply()
        }
        if(savedLocations.size == 1) {
            saveSelectedLocation(location);
        }
    }

    fun getSavedLocations(): List<LocationResponse> {
        val json = sharedPreferences.getString(LOCATIONS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<LocationResponse>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    fun removeLocation(location: LocationResponse) {
        val savedLocations = getSavedLocations().toMutableList()
        savedLocations.removeAll { it.name == location.name }
        val json = gson.toJson(savedLocations)
        sharedPreferences.edit().putString(LOCATIONS_KEY, json).apply()


        sharedPreferences.edit()
            .remove("$WEATHER_DATA_KEY${location.name}")
            .apply()

        // Если удаляемая локация была выбранной, сбрасываем выбор
        if (getSelectedLocation()?.name == location.name) {
            clearSelectedLocation()
        }
    }

    fun setFlag(value: Boolean) {
        sharedflag.edit().putBoolean("MY_FLAG_KEY", value).apply()
    }

    fun getFlag(): Boolean {
        return sharedflag.getBoolean("MY_FLAG_KEY", false) // false - значение по умолчанию
    }



}