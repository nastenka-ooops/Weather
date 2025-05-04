package com.example.weather.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.weather.api.OpenMeteoApi
import com.example.weather.dto.LocationResponse
import com.example.weather.dto.WeatherResponse
import com.example.weather.utils.WeatherUtils
import com.example.whether.databinding.LocationWeatherLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weather.utils.SharedPreferencesHelper
class LocationWeatherActivity : ComponentActivity() {
    private lateinit var binding: LocationWeatherLayoutBinding
    private var weatherUtils: WeatherUtils = WeatherUtils()
    private lateinit var location: LocationResponse
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationWeatherLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        location = (intent.getSerializableExtra("location") as? LocationResponse)!!

        binding.weatherLayout.tvCityName.text = location.name
        fetchWeatherData(location.latitude, location.longitude)

        binding.btnBack.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnAdd.setOnClickListener {
            sharedPreferencesHelper.saveLocation(location)
            Toast.makeText(this, "Location added to your list", Toast.LENGTH_SHORT).show()
        }
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
        binding.weatherLayout.apply {
            val isDay = weatherData.current_weather.is_day == 1

            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(
                    weatherData.current_weather.weathercode,
                    isDay
                )
            )

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

        var remainingHours = (remainingInMinutes / 60)
        if (remainingHours < 0) remainingHours = 0
        var remainingMins = (remainingInMinutes % 60)
        if (remainingMins < 0) remainingMins = 0

        return "${remainingHours}H ${remainingMins}M"
    }

}