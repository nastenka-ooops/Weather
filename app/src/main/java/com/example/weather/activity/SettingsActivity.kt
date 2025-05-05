package com.example.weather.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.adapter.SavedLocationsAdapter
import com.example.weather.dto.LocationResponse
import com.example.weather.utils.SharedPreferencesHelper
import com.example.whether.databinding.SettingsLayoutBinding

class SettingsActivity : ComponentActivity() {
    private lateinit var binding: SettingsLayoutBinding
    private lateinit var adapter: SavedLocationsAdapter
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var selectedLocation: LocationResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        setupRecyclerView()
        loadSavedLocations()

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