package com.example.weather.activity

import ChosenUnits
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.content.res.ColorStateList
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
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
import com.example.weather.utils.WeatherUtils
import com.example.whether.R
import com.example.whether.databinding.LocationWeatherLayoutBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalTime
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weather.utils.SharedPreferencesHelper


class LocationWeatherActivity : ComponentActivity() {
    private lateinit var binding: LocationWeatherLayoutBinding
    private lateinit var hourlyAdapter: HourlyWeatherAdapter
    private lateinit var dailyAdapter: DailyWeatherAdapter
    private var weatherUtils: WeatherUtils = WeatherUtils()
    private lateinit var location: LocationResponse
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var chosenUnits: ChosenUnits

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LocationWeatherLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)
        chosenUnits = ChosenUnits(this)
        location = (intent.getSerializableExtra("location") as? LocationResponse)!!
        updateAddButtonState()
        binding.weatherLayout.tvCityName.text = location.name
        fetchWeatherData(location.latitude, location.longitude)

        binding.btnBack.setOnClickListener {
//            val intent = Intent(this, SearchActivity::class.java)
//            startActivity(intent)
            finish()
        }

        binding.llAddToList.setOnClickListener{
        //binding.btnAdd.setOnClickListener {
            val isCurrentlySaved = sharedPreferencesHelper.isSavedLockation(location)
            if(sharedPreferencesHelper.isSavedLockation(location))
            {
                // Если город уже в списке - удаляем его
                sharedPreferencesHelper.removeLocation(location)
                Toast.makeText(this, "Location removed from your list", Toast.LENGTH_SHORT).show()
            }
            else
            {
                // Если города нет в списке - добавляем
                sharedPreferencesHelper.saveLocation(location)
                Toast.makeText(this, "Location added to your list", Toast.LENGTH_SHORT).show()
            }
            // Анимированное переключение состояний
            updateAddButtonState()
            binding.btnAdd.animate()
                .scaleX(0.8f)
                .scaleY(0.8f)
                .setDuration(100)
                .withEndAction {
                    binding.btnAdd.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .start()
                }
                .start()
            //sharedPreferencesHelper.saveLocation(location)
            //Toast.makeText(this, "Location added to your list", Toast.LENGTH_SHORT).show()
        }

        updateAddButtonState()

    }


    private fun updateAddButtonState() {
        if (sharedPreferencesHelper.isSavedLockation(location)) {
            binding.btnAdd.setImageResource(R.drawable.ic_check)
            binding.tvAddToList.text = getString(R.string.added_to_list)
            binding.tvAddToList.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.llAddToList.setBackgroundResource(R.drawable.green_rounded_button)
        } else {
            binding.btnAdd.setImageResource(R.drawable.ic_add_location)
            binding.tvAddToList.text = getString(R.string.add_to_list)
            binding.tvAddToList.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.llAddToList.setBackgroundResource(R.drawable.button_add_to_list_bg)
        }
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


    @SuppressLint("SetTextI18n")
    private fun updateUI(weatherData: WeatherResponse, airQualityData: AirQualityResponse) {
        val currentTime: String
        binding.weatherLayout.apply {
            val isDay = weatherData.current_weather.is_day == 1

            ivWeatherIcon.setImageResource(
                weatherUtils.getWeatherIcon(
                    weatherData.current_weather.weathercode,
                    isDay
                )
            )

            tvTemperature.text = "${weatherData.current_weather.temperature} ${weatherData.current_weather_units.temperature}"

            currentTime = weatherData.current_weather.time.substring(11, 16)

            val sunset = weatherData.daily.sunset[0].substring(11, 16)

            val daylight = weatherData.daily.daylight_duration[0]
            val daylightHours = (daylight / 3600).toInt()
            val daylightMinutes = ((daylight - daylightHours * 3600) / 60).toInt()
            val remainingDaylight = calculateRemainingDaylight(sunset, currentTime)

            tvLengthOfDay.text = "${daylightHours}H ${daylightMinutes}M"
            tvRemDaylight.text = remainingDaylight

            val sunriseTime = weatherData.daily.sunrise[0].substring(11, 16)
            binding.weatherLayout.sunPositionView.updateTime(sunriseTime, sunset, LocalTime.now())

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
            .filter { index ->
                val hour = weatherData.hourly.time[index].substring(11, 13).toInt()
                hour >= currentTime.substring(0, 2).toInt()
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
                "${weatherData.current_weather.windspeed} ${weatherData.current_weather_units.windspeed}",
                "Gusts ${weatherData.daily.wind_gusts_10m_max[0]} ${weatherData.current_weather_units.windspeed}"
            ),
            WeatherDetail(
                "Humidity",
                "${weatherData.hourly.relative_humidity_2m[currentIndex]}%",
                "The dew point is ${weatherData.hourly.dew_point_2m[currentIndex]}"+
                        "${weatherData.hourly_units.dew_point_2m} right now"
            ),
            WeatherDetail(
                "Pressure",
                "${weatherData.hourly.surface_pressure[currentIndex]} "+
                        weatherData.hourly_units.surface_pressure,
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
                "${"%.2f".format(weatherData.daily.precipitation_sum.sum())} mm is expected in next 7 days"
            ),
            WeatherDetail(
                "Air Quality Index",
                "${airQualityData.current.european_aqi}",
                AirQualityUtils.getAqiDescription(airQualityData.current.european_aqi)
            ),
            WeatherDetail(
                "UV Index",
                "${weatherData.hourly.uv_index[currentIndex]}",
                "The maximum UV index is ${weatherData.daily.uv_index_max[0]}"
            ),
            WeatherDetail(
                "Rain probability",
                "${weatherData.hourly.rain[currentIndex]} %",
                ""
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