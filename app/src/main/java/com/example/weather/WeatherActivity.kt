package com.example.weather

import LocationHelper
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.weather.api.OpenMeteoApi
import com.example.weather.dto.WeatherResponse
import com.example.whether.databinding.WeatherLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherActivity : ComponentActivity() {
    private lateinit var binding: WeatherLayoutBinding
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WeatherLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)
        locationHelper.getCurrentLocation(
            onSuccess = { lat, lon ->
                val cityName = locationHelper.getCityName(lat, lon)
                binding.tvCityName.text = cityName
                fetchWeatherData(lat, lon)
            }
        )
    }

    private fun fetchWeatherData(lat: Double, lon: Double) {
        lifecycleScope.launch {
            try {
                val weatherData = withContext(Dispatchers.IO) {
                    val retrofit = Retrofit.Builder()
                        .baseUrl("https://api.open-meteo.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()

                    val service = retrofit.create(OpenMeteoApi::class.java)
                    service.getCurrentWeather(lat, lon)
                }

                updateUI(weatherData)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: WeatherResponse) {
        binding.apply {

            tvTemperature.text = "${weatherData.current_weather.temperature}°"

            val currentTime = weatherData.current_weather.time.substring(11, 16)

            tvTime.text = currentTime

            tvUv.text = weatherData.daily.uv_index_max[0].toString()

            tvRain.text = "${weatherData.daily.precipitation_probability_mean[0]}%"

            tvWind.text = weatherData.current_weather.windspeed.toString()

            tvSunrise.text = weatherData.daily.sunrise[0].substring(11, 16)
            val sunset = weatherData.daily.sunset[0].substring(11, 16);
            tvSunset.text = sunset

            val daylight = weatherData.daily.daylight_duration[0]
            val daylightHours = (daylight / 3600).toInt()
            val daylightMinutes = ((daylight - daylightHours * 3600) / 60).toInt()
            tvLengthUfDay.text = "${daylightHours}H ${daylightMinutes}M"

            val remainingDaylight = calculateRemainingDaylight(sunset, currentTime)
            tvRemainingDaylight.text = remainingDaylight
        }
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

        val remainingHours = (remainingInMinutes / 60)
        val remainingMins = (remainingInMinutes % 60)

        return "${remainingHours}H ${remainingMins}M"
    }
}