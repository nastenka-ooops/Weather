package com.example.weather.activity

import LocationUtils
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.adapter.AspectAdapter
import com.example.weather.adapter.PlanetAdapter
import com.example.weather.api.AstrologyApi
import com.example.weather.dto.astro.AspectsResponse
import com.example.weather.dto.astro.NatalWheelChartResponse
import com.example.weather.dto.astro.PlanetsResponse
import com.example.whether.databinding.AstroForecastLayputBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.TimeZone

class AstroForecastActivity: ComponentActivity() {
    private lateinit var binding: AstroForecastLayputBinding
    private lateinit var planetAdapter: PlanetAdapter
    private lateinit var aspectAdapter: AspectAdapter
    private lateinit var locationUtils: LocationUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AstroForecastLayputBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationUtils = LocationUtils(this)

        planetAdapter = PlanetAdapter()

        binding.rvPlanets.layoutManager = LinearLayoutManager(this)
        binding.rvPlanets.adapter = planetAdapter

        var planets: PlanetsResponse? = null

        aspectAdapter = AspectAdapter()

        binding.rvPlanets.layoutManager = LinearLayoutManager(this)
        binding.rvPlanets.adapter = planetAdapter

        var aspects: AspectsResponse? = null
        var natalWheelChart: NatalWheelChartResponse? = null

        lifecycleScope.launch {
            try {
                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH) + 1 // Месяцы в Calendar от 0 до 11
                val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
                val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                val currentMinute = calendar.get(Calendar.MINUTE)
                val currentSecond = calendar.get(Calendar.SECOND)
                var latitude = 52.52
                var longitude = 13.41

                locationUtils.getCurrentLocation(
                    onSuccess = { lat, lon ->
                        latitude = lat
                        longitude = lon
                    }
                )

                val timeZone = TimeZone.getDefault()
                val timezoneOffset = timeZone.getOffset(System.currentTimeMillis()) / 1000 / 60 / 60f

                planets = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://json.freeastrologyapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(AstrologyApi::class.java)
                    service.getPlanets(
                        year = currentYear,
                        month = currentMonth,
                        date = currentDay,
                        hours = currentHour,
                        minutes = currentMinute,
                        seconds = currentSecond,
                        latitude = latitude, // Ваша переменная с широтой
                        longitude = longitude, // Ваша переменная с долготой
                        timezone = timezoneOffset,
                    )
                }

                aspects = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://json.freeastrologyapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(AstrologyApi::class.java)
                    service.getAspects(
                        year = currentYear,
                        month = currentMonth,
                        date = currentDay,
                        hours = currentHour,
                        minutes = currentMinute,
                        seconds = currentSecond,
                        latitude = latitude, // Ваша переменная с широтой
                        longitude = longitude, // Ваша переменная с долготой
                        timezone = timezoneOffset,
                    )
                }

                natalWheelChart = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://json.freeastrologyapi.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(AstrologyApi::class.java)
                    service.getNatalWheelChart(
                        year = currentYear,
                        month = currentMonth,
                        date = currentDay,
                        hours = currentHour,
                        minutes = currentMinute,
                        seconds = currentSecond,
                        latitude = latitude, // Ваша переменная с широтой
                        longitude = longitude, // Ваша переменная с долготой
                        timezone = timezoneOffset,
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        planetAdapter.setPlanetsList(planets!!.output)
        aspectAdapter.setAspectsList(aspects!!.output)
        binding.ivNatalWheelChart.setImageURI(natalWheelChart!!.output.toUri())
    }
}