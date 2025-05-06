package com.example.weather.activity

import ChosenUnits
import LocationUtils
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.adapter.SavedLocationsAdapter
import com.example.weather.dto.LocationResponse
import com.example.weather.utils.SharedPreferencesHelper
import com.example.whether.R
import com.example.whether.databinding.SettingsLayoutBinding

class SettingsActivity : ComponentActivity() {
    private lateinit var binding: SettingsLayoutBinding
    private lateinit var adapter: SavedLocationsAdapter
    private lateinit var locationUtils: LocationUtils
    private lateinit var currLocation:LocationResponse
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var chosenUnits: ChosenUnits
    private var selectedLocation: LocationResponse? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        locationUtils = LocationUtils(this)
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        chosenUnits = ChosenUnits(this)

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

        val choiceGroup1: RadioGroup = findViewById(R.id.choiceGroup1)
        val choiceGroup2: RadioGroup = findViewById(R.id.choiceGroup2)

        restoreRadioGroupSelections(choiceGroup1, choiceGroup2)

        // Установка слушателей изменений
        choiceGroup1.setOnCheckedChangeListener { _, checkedId ->
            findViewById<RadioButton>(checkedId)?.text?.toString()?.let {
                chosenUnits.temperatureUnit = it
            }
        }

        choiceGroup2.setOnCheckedChangeListener { _, checkedId ->
            findViewById<RadioButton>(checkedId)?.text?.toString()?.let {
                chosenUnits.windSpeedUnit = it
            }
        }



    }


    private fun restoreRadioGroupSelections(
        group1: RadioGroup,
        group2: RadioGroup
    ) {
        // Для Temperature
        selectRadioButtonByText(group1, chosenUnits.temperatureUnit)

        // Для Wind speed
        selectRadioButtonByText(group2, chosenUnits.windSpeedUnit)

    }
    private fun selectRadioButtonByText(radioGroup: RadioGroup, text: String) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as RadioButton
            if (radioButton.text.toString() == text) {
                radioButton.isChecked = true
                break
            }
        }
    }

    private fun resetToDefaultSettings(
        group1: RadioGroup,
        group2: RadioGroup,
        group3: RadioGroup
    ) {
        // Сброс значений
        chosenUnits.temperatureUnit = "°C"
        chosenUnits.windSpeedUnit = "m/s"
        chosenUnits.pressureUnit = "mmHg"

        // Обновление UI
        restoreRadioGroupSelections(group1, group2)
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