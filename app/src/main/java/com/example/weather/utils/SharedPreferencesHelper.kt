package com.example.weather.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.weather.dto.LocationResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WeatherAppPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val LOCATIONS_KEY = "saved_locations"

    fun saveLocation(location: LocationResponse) {
        val savedLocations = getSavedLocations().toMutableList()

        // Проверяем, нет ли уже такой локации в списке
        if (!savedLocations.any { it.name == location.name }) {
            savedLocations.add(location)
            val json = gson.toJson(savedLocations)
            sharedPreferences.edit().putString(LOCATIONS_KEY, json).apply()
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
    }
}