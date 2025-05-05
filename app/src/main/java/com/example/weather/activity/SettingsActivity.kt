package com.example.weather.activity

import LocationUtils
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.adapter.SavedLocationsAdapter
import com.example.weather.api.OpenMeteoApi
import com.example.weather.dto.LocationResponse
import com.example.weather.model.Location
import com.example.weather.utils.SharedPreferencesHelper
import com.example.whether.databinding.SettingsLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SettingsActivity : ComponentActivity() {
    private lateinit var binding: SettingsLayoutBinding
    private lateinit var adapter: SavedLocationsAdapter
    private lateinit var locationUtils: LocationUtils
    private lateinit var currLocation:LocationResponse
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var selectedLocation: LocationResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationUtils = LocationUtils(this)
        sharedPreferencesHelper = SharedPreferencesHelper(this)

        setupRecyclerView()
        loadSavedLocations()
        binding.btnResetDefault.setOnClickListener {
            sharedPreferencesHelper.clearSelectedLocation();
            sharedPreferencesHelper.setSelectedNull();
            loadSavedLocations()
            Toast.makeText(this, "Home weather changed to current place", Toast.LENGTH_SHORT).show()
        }
        binding.btnBack.setOnClickListener {
            finish()
        }

        selectedLocation = sharedPreferencesHelper.getSelectedLocation()
        loadSavedLocations()
    }





    private fun setupRecyclerView() {
        adapter = SavedLocationsAdapter(
            onLocationClick = { location ->
                // Переход к просмотру погоды
                val intent = Intent(this, LocationWeatherActivity::class.java)
                intent.putExtra("location", location)
                startActivity(intent)
            },
            onSetDefaultClick = { location ->
                // Установка как основной локации
                sharedPreferencesHelper.saveSelectedLocation(location)
                Toast.makeText(this, "${location.name} set as default", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { location ->
                // Удаление локации
                if (sharedPreferencesHelper.getSelectedLocation()?.name == location.name) {
                    sharedPreferencesHelper.clearSelectedLocation()
                }
                sharedPreferencesHelper.removeLocation(location)
                loadSavedLocations()
            },
            sharedPreferencesHelper = sharedPreferencesHelper
        )

        binding.rvSavedLocations.layoutManager = LinearLayoutManager(this)
        binding.rvSavedLocations.adapter = adapter
    }

    private fun loadSavedLocations() {
        val savedLocations = sharedPreferencesHelper.getSavedLocations()
        adapter.setLocationsList(savedLocations)

        if (savedLocations.isEmpty()) {
            binding.tvNoLocations.visibility = View.VISIBLE
            binding.rvSavedLocations.visibility = View.GONE
        } else {
            binding.tvNoLocations.visibility = View.GONE
            binding.rvSavedLocations.visibility = View.VISIBLE
        }
    }
}