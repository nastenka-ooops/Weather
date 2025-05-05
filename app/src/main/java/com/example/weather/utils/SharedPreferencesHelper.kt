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
    private val SELECTED_LOCATION_KEY = "selected_location"


    fun saveSelectedLocation(location: LocationResponse) {
        val json = gson.toJson(location)
        sharedPreferences.edit().putString(SELECTED_LOCATION_KEY, json).apply()
    }



    fun getSelectedLocation(): LocationResponse? {
        val json = sharedPreferences.getString(SELECTED_LOCATION_KEY, null)
        return if (json != null) {
            gson.fromJson(json, LocationResponse::class.java)
        } else {
            null
        }
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
    }


    fun saveLocation(location: LocationResponse) {
        val savedLocations = getSavedLocations().toMutableList()

        // Проверяем, нет ли уже такой локации в списке
        if (!savedLocations.any { it.name == location.name }) {
            savedLocations.add(location)
            val json = gson.toJson(savedLocations)
            sharedPreferences.edit().putString(LOCATIONS_KEY, json).apply()
        }
        if(savedLocations.size == 1)
            saveSelectedLocation(location);
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