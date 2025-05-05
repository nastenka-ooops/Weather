package com.example.weather.activity

import LocationUtils
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.caverock.androidsvg.SVG
import com.example.weather.adapter.AspectAdapter
import com.example.weather.adapter.PlanetAdapter
import com.example.weather.api.AstrologyApi
import com.example.weather.dto.astro.AspectsResponse
import com.example.weather.dto.astro.AstroRequest
import com.example.weather.dto.astro.NatalWheelChartResponse
import com.example.weather.dto.astro.PlanetsResponse
import com.example.whether.databinding.AstroForecastLayputBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URL
import java.util.Calendar
import java.util.Properties
import java.util.TimeZone

class AstroForecastActivity : ComponentActivity() {
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

        aspectAdapter = AspectAdapter()

        binding.rvAspects.layoutManager = LinearLayoutManager(this)
        binding.rvAspects.adapter = aspectAdapter

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth =
            calendar.get(Calendar.MONTH) + 1 // Месяцы в Calendar от 0 до 11
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
        val timezoneOffset =
            timeZone.getOffset(System.currentTimeMillis()) / 1000 / 60 / 60f

        val request = AstroRequest(
            year = currentYear,
            month = currentMonth,
            date = currentDay,
            hours = currentHour,
            minutes = currentMinute,
            seconds = currentSecond,
            latitude = latitude,
            longitude = longitude,
            timezone = timezoneOffset,
        )

        lifecycleScope.launch {
            val planetsDeferred = async {
                withContext(Dispatchers.IO) {
                    try {
                        val okHttpClient = OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                val request = chain.request().newBuilder()
                                    .header(
                                        "x-api-key",
                                        getLocalProperty("ASTROLOGY_API_TOKEN")!!
                                    )
                                    .build()
                                chain.proceed(request)
                            }
                            .build()

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://json.freeastrologyapi.com/")
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        retrofit.create(AstrologyApi::class.java).getPlanets(request)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            delay(2000)

            // Fetch aspects
            val aspectsDeferred = async {
                withContext(Dispatchers.IO) {
                    try {
                        val okHttpClient = OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                val request = chain.request().newBuilder()
                                    .header(
                                        "x-api-key",
                                        getLocalProperty("ASTROLOGY_API_TOKEN")!!
                                    )
                                    .build()
                                chain.proceed(request)
                            }
                            .build()

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://json.freeastrologyapi.com/")
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        val modifiedRequest = request.copy(
                            config = request.config.toMutableMap().apply {
                                put("allowed_aspects", listOf("Conjunction", "Trine", "Sextile"))
                                put("exclude_planets", listOf(
                                    "Lilith", "Chiron", "Ceres", "Vesta", "Juno", "Pallas",
                                    "True Node", "Mean Node", "IC", "MC", "Descendant", "Ascendant"))
                            }
                        )
                        retrofit.create(AstrologyApi::class.java).getAspects(modifiedRequest)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            delay(2000)

            // Fetch natal wheel chart
            val natalWheelChartDeferred = async {
                withContext(Dispatchers.IO) {
                    try {
                        val okHttpClient = OkHttpClient.Builder()
                            .addInterceptor { chain ->
                                val request = chain.request().newBuilder()
                                    .header(
                                        "x-api-key",
                                        getLocalProperty("ASTROLOGY_API_TOKEN")!!
                                    )
                                    .build()
                                chain.proceed(request)
                            }
                            .build()

                        val retrofit = Retrofit.Builder()
                            .baseUrl("https://json.freeastrologyapi.com/")
                            .client(okHttpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build()

                        retrofit.create(AstrologyApi::class.java).getNatalWheelChart(request)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }

            // Wait for all requests to complete
            val planets = planetsDeferred.await()
            val aspects = aspectsDeferred.await()
            val natalWheelChart = natalWheelChartDeferred.await()

            // Update UI only if responses are not null
            planets?.let { planetAdapter.setPlanetsList(it.output) }
            aspects?.let { aspectAdapter.setAspectsList(it.output) }
            lifecycleScope.launch {
                try {
                    // Load SVG in IO dispatcher
                    val svgUrl = natalWheelChart?.output ?: return@launch
                    val bitmap = withContext(Dispatchers.IO) {
                        val svg = SVG.getFromInputStream(URL(svgUrl).openStream())
                        val picture = svg.renderToPicture()
                        Bitmap.createBitmap(picture.width, picture.height, Bitmap.Config.ARGB_8888).apply {
                            Canvas(this).drawPicture(picture)
                        }
                    }

                    // Switch back to Main thread to update UI
                    binding.ivNatalWheelChart.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        binding.btnBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getLocalProperty(key: String): String? {
        val properties = Properties()
        val localPropertiesFile = File("local.properties")
        if (localPropertiesFile.exists()) {
            properties.load(localPropertiesFile.inputStream())
            return properties.getProperty(key)
        }
        return "YW2fAxM78pahTLwgsMO7M2tCMQKOZghN8if6S7CB"
    }
}