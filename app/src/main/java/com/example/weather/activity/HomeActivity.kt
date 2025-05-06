package com.example.weather.activity

import ChosenUnits
import LocationUtils
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.adapter.DailyWeatherAdapter
import com.example.weather.adapter.HourlyWeatherAdapter
import com.example.weather.adapter.WeatherDetailAdapter
import com.example.weather.api.OpenMeteoApi
import com.example.weather.dto.*
import com.example.weather.utils.AirQualityUtils
import com.example.weather.utils.SharedPreferencesHelper
import com.example.weather.utils.WeatherUtils
import com.example.whether.R
import com.example.whether.databinding.HomeLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.threeten.bp.LocalTime

class HomeActivity : ComponentActivity() {
    private lateinit var binding: HomeLayoutBinding
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter
    private lateinit var locationUtils: LocationUtils
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var chosenUnits: ChosenUnits
    private var weatherUtils: WeatherUtils = WeatherUtils()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = HomeLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)


        sharedPreferencesHelper = SharedPreferencesHelper(this)
        chosenUnits = ChosenUnits(this)
        locationUtils = LocationUtils(this)
        val selectedLocation = sharedPreferencesHelper.getSelectedLocation()
//        if(sharedPreferencesHelper.getFlag()) {
//            locationUtils.getCurrentLocation(
//                onSuccess = { lat, lon ->
//                    val cityName = locationUtils.getCityName(lat, lon)
//                    binding.weatherLayout.tvCityName.text = cityName
//                    fetchWeatherData(lat, lon)
//                }
//            )
//        }
        binding.etSearchLocation.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        binding.btnAstrology.setOnClickListener{
            val intent = Intent(this, AstroForecastActivity::class.java)
            startActivity(intent)
            finish()
        }
        loadWeatherData()
//        if (selectedLocation != null) {
//            showSelectedLocation(selectedLocation)
//        } else {
//            getCurrentLocation()
//        }
    }

    private fun loadWeatherData() {
        val selectedLocation = sharedPreferencesHelper.getSelectedLocation()

        if (selectedLocation != null) {
            // Пытаемся загрузить сохраненные данные
            val weatherData = sharedPreferencesHelper.getWeatherData(selectedLocation)
            val airQualityData = sharedPreferencesHelper.getAirQualityData(selectedLocation)
            if (weatherData != null && airQualityData != null) {
                // Показываем сохраненные данные
                updateUI(weatherData, airQualityData)
            } else {
                // Загружаем свежие данные с API
                fetchFreshData(selectedLocation)
            }
        } else {
            // Используем текущее местоположение
            locationUtils.getCurrentLocation(
                onSuccess = { lat, lon ->
                    val cityName = locationUtils.getCityName(lat, lon)
                    binding.weatherLayout.tvCityName.text = cityName
                    fetchWeatherData(lat, lon)
                }
            )
        }
    }


    private fun showSelectedLocation(location: LocationResponse) {
        binding.weatherLayout.tvCityName.text = location.name
        fetchWeatherData(location.latitude, location.longitude)
    }

    override fun onResume() {
        super.onResume()
        if(!sharedPreferencesHelper.getFlag()) {
            val selectedLocation = sharedPreferencesHelper.getSelectedLocation()
            selectedLocation?.let {
                showSelectedLocation(it)
            }
        }
        else{
            locationUtils.getCurrentLocation(
                onSuccess = { lat, lon ->
                    val cityName = locationUtils.getCityName(lat, lon)
                    binding.weatherLayout.tvCityName.text = cityName
                    fetchWeatherData(lat, lon)
                }
            )
        }
    }

    private fun getCurrentLocation() {
        locationUtils.getCurrentLocation(
            onSuccess = { lat, lon ->
                val cityName = locationUtils.getCityName(lat, lon)
                binding.weatherLayout.tvCityName.text = cityName
                fetchWeatherData(lat, lon)
            }
        )
    }


    private fun fetchWeatherData(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val weatherRetrofit = Retrofit.Builder()
                    .baseUrl("https://api.open-meteo.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val airQualityRetrofit = Retrofit.Builder()
                    .baseUrl("https://air-quality-api.open-meteo.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherService = weatherRetrofit.create(OpenMeteoApi::class.java)
                val airQualityService = airQualityRetrofit.create(OpenMeteoApi::class.java)

                val (weatherData, airQualityData) = withContext(Dispatchers.IO) {
                    val weather = weatherService.getCurrentWeather(
                        lat= lat,
                        lon = lon,
                        windSpeedUnit = chosenUnits.getApiWindSpeedUnit(),
                        temperatureUnit = chosenUnits.getApiTemperatureUnit()
                    )
                    val airQuality = airQualityService.getAirQuality(lat, lon)
                    Pair(weather, airQuality)
                }

                updateUI(weatherData, airQualityData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getApiTemperatureUnit(prefUnit: String): String {
        return when (prefUnit) {
            "°C" -> "celsius"
            "°F" -> "fahrenheit"
            else -> "celsius"
        }
    }

    private fun getApiWindSpeedUnit(prefUnit: String): String {
        return when (prefUnit) {
            "m/s" -> "ms"
            "km/h" -> "kmh"
            "mph" -> "mph"
            "knots" -> "kn"
            else -> "ms"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: WeatherResponse, airQualityData: AirQualityResponse) {
        val currentTime: String
        val sunset: String
        binding.weatherLayout.apply {
            val isDay = weatherData.current_weather.is_day == 1

            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(
                    weatherData.current_weather.weathercode,
                    isDay
                )
            )

            tvTemperature.text = "${weatherData.current_weather.temperature}°"

            currentTime = weatherData.current_weather.time.substring(11, 16)
            tvTime.text = currentTime

            tvUv.text = weatherData.daily.uv_index_max[0].toString()

            tvRain.text = "${weatherData.daily.precipitation_probability_mean[0]}%"

            tvWind.text = weatherData.current_weather.windspeed.toString()

            tvSunrise.text = weatherData.daily.sunrise[0].substring(11, 16)
            sunset = weatherData.daily.sunset[0].substring(11, 16);
            tvSunset.text = sunset

            val daylight = weatherData.daily.daylight_duration[0]
            val daylightHours = (daylight / 3600).toInt()
            val daylightMinutes = ((daylight - daylightHours * 3600) / 60).toInt()
            tvLengthUfDay.text = "${daylightHours}H ${daylightMinutes}M"

            val remainingDaylight = calculateRemainingDaylight(sunset, currentTime)
            tvRemainingDaylight.text = remainingDaylight

            tvLengthOfDay.text = "${daylightHours}H ${daylightMinutes}M"
            tvRemDaylight.text = remainingDaylight

            val sunriseTime = weatherData.daily.sunrise[0].substring(11, 16)
            val sunsetTime = weatherData.daily.sunset[0].substring(11, 16)
            binding.weatherLayout.sunPositionView.updateTime(sunriseTime, sunsetTime, LocalTime.now())

        }

        hourlyAdapter = HourlyWeatherAdapter()

        binding.weatherLayout.rvHourlyForecast.layoutManager =
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        binding.weatherLayout.rvHourlyForecast.adapter = hourlyAdapter
        val hourlyWeatherItems = weatherData.hourly.time.indices
            .dropWhile { index ->
                val hour = weatherData.hourly.time[index].substring(11, 13).toInt()
                hour < currentTime.substring(0,2).toInt()
            }
            .map { index ->
                HourlyWeatherItem(
                    time = weatherData.hourly.time[index],
                    temperature = weatherData.hourly.temperature_2m[index],
                    weatherCode = weatherData.hourly.weather_code[index],
                    isDay = weatherData.hourly.is_day[index]
                )
            }
        hourlyAdapter.setWeatherList(hourlyWeatherItems)

        dailyAdapter = DailyWeatherAdapter()

        binding.weatherLayout.rvDailyForecast.layoutManager = LinearLayoutManager(this)
        binding.weatherLayout.rvDailyForecast.adapter = dailyAdapter
        val dailyWeatherItems = weatherData.daily.time.indices
            .map { index ->
                DailyWeatherItem(
                    time = weatherData.daily.time[index],
                    temperatureDay = weatherData.daily.temperature_2m_max[index],
                    temperatureNight = weatherData.daily.temperature_2m_min[index],
                    weatherCode = weatherData.daily.weather_code[index]
                )
            }
        dailyAdapter.setWeatherList(dailyWeatherItems)


        val currentIndex = weatherData.hourly.time.indexOfFirst {
            it.substring(11, 16) == currentTime
        }.takeIf { it != -1 } ?: 0 // fallback на 0, если не найдено

        val weatherDetails = listOf(
            WeatherDetail(
                "Wind",
                "${weatherData.current_weather.windspeed} km/h",
                "Gusts ${weatherData.daily.wind_gusts_10m_max[0]} km/h"
            ),
            WeatherDetail(
                "Humidity",
                "${weatherData.hourly.relative_humidity_2m[currentIndex]}%",
                "The dew point is ${weatherData.hourly.dew_point_2m[currentIndex]}°C right now"
            ),
            WeatherDetail(
                "Pressure",
                "${weatherData.hourly.surface_pressure[currentIndex]} hPa",
                ""
            ),
            WeatherDetail(
                "Visibility",
                "${weatherData.hourly.visibility[currentIndex] / 1000} km",
                ""
            ),
            WeatherDetail(
                "Precipitation",
                "${weatherData.daily.precipitation_sum[0]} mm",
                "${weatherData.daily.precipitation_sum.sum()} mm is expected in next 7 days"
            ),
            WeatherDetail(
                "Air Quality Index",
                "${airQualityData.current.european_aqi}",
                AirQualityUtils.getAqiDescription(airQualityData.current.european_aqi)

            )
        )

        val recyclerView = findViewById<RecyclerView>(R.id.weatherDetailsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(
            this,
            2, // Кол-во строк
            GridLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = WeatherDetailAdapter(weatherDetails)


        val daylight = weatherData.daily.daylight_duration[0]
        val daylightHours = (daylight / 3600).toInt()
        val daylightMinutes = ((daylight - daylightHours * 3600) / 60).toInt()



    }

    private fun calculateRemainingDaylight(sunset: String, curent: String): String {
        val sunsetParts = sunset.split(":")
        val sunsetHour = sunsetParts[0].toInt()
        val sunsetMin = sunsetParts[1].toInt()
        val sunsetInMinutes = sunsetHour * 60 + sunsetMin

        val currentParts = curent.split(":")
        val currentHour = currentParts[0].toInt()
        val currentMin = currentParts[1].toInt()
        val currentInMinutes = currentHour * 60 + currentMin

        val remainingInMinutes = sunsetInMinutes - currentInMinutes

        var remainingHours = (remainingInMinutes / 60)
        if (remainingHours < 0) remainingHours = 0
        var remainingMins = (remainingInMinutes % 60)
        if (remainingMins < 0) remainingMins = 0

        return "${remainingHours}H ${remainingMins}M"
    }




    private fun fetchFreshData(location: LocationResponse) {
        lifecycleScope.launch {
            try {
                val weatherRetrofit = Retrofit.Builder()
                    .baseUrl("https://api.open-meteo.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val airQualityRetrofit = Retrofit.Builder()
                    .baseUrl("https://air-quality-api.open-meteo.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val weatherService = weatherRetrofit.create(OpenMeteoApi::class.java)
                val airQualityService = airQualityRetrofit.create(OpenMeteoApi::class.java)

                val (weatherData, airQualityData) = withContext(Dispatchers.IO) {
                    val weather = weatherService.getCurrentWeather(
                        lat= location.latitude,
                        lon = location.longitude,
                        windSpeedUnit = chosenUnits.getApiWindSpeedUnit(),
                        temperatureUnit = chosenUnits.getApiTemperatureUnit()
                    )
                    val airQuality = airQualityService.getAirQuality(location.latitude, location.longitude)
                    Pair(weather, airQuality)
                }
                sharedPreferencesHelper.saveWeatherData(location, weatherData, airQualityData)

                updateUI(weatherData, airQualityData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}