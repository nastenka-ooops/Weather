package com.example.weather.activity

import ChosenUnits
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.adapter.SearchLocationsAdapter
import com.example.weather.api.OpenMeteoApi
import com.example.whether.databinding.SearchLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : ComponentActivity() {
    private lateinit var binding: SearchLayoutBinding
    private lateinit var adapter: SearchLocationsAdapter
    private lateinit var chosenUnits: ChosenUnits

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etSearchLocation.requestFocus()
        chosenUnits = ChosenUnits(this)
        adapter = SearchLocationsAdapter { selectedLocation ->
            val intent = Intent(this, LocationWeatherActivity::class.java)
            intent.putExtra("location", selectedLocation)
            startActivity(intent)
        }

        binding.rvResults.layoutManager = LinearLayoutManager(this)
        binding.rvResults.adapter = adapter

        binding.etSearchLocation.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim()
                if (!query.isNullOrEmpty() && query.length >= 3) { // Ищем только при 3+ символах
                    searchLocations(query)
                } else {
                    adapter.setLocationsList(emptyList()) // Очищаем результаты
                }
            }
        })

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun searchLocations(query: String) {
        lifecycleScope.launch {
            try {
                val locations = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://geocoding-api.open-meteo.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(OpenMeteoApi::class.java)
                    service.getLocations(query) // Добавляем параметр для 10 результатов
                }

                locations.results.let { results ->
                    // Получаем погоду для каждого места (параллельно)
                    val locationsWithWeather = results.map { location ->
                        async {
                            try {
                                val weatherData = withContext(Dispatchers.IO) {
                                    val retrofit = Retrofit.Builder()
                                        .baseUrl("https://api.open-meteo.com/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build()

                                    val service = retrofit.create(OpenMeteoApi::class.java)
                                    service.getCurrentWeather(
                                        location.latitude,
                                        location.longitude,
                                        windSpeedUnit = chosenUnits.getApiWindSpeedUnit(),
                                        temperatureUnit = chosenUnits.getApiTemperatureUnit())
                                }
                                location.apply {
                                    temperature = weatherData.current_weather.temperature
                                    weatherCode = weatherData.current_weather.weathercode
                                    isDay = weatherData.current_weather.is_day
                                }
                            } catch (e: Exception) {
                                location // Возвращаем location без данных о погоде в случае ошибки
                            }
                        }
                    }.awaitAll()

                    // Обновляем UI
                    adapter.setLocationsList(locationsWithWeather)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                adapter.setLocationsList(emptyList()) // Очищаем при ошибке
            }
        }
    }
}