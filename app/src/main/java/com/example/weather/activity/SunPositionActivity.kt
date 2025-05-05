package com.example.weather.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.weather.view.SunPositionView
import com.example.whether.databinding.ActivitySunPositionBinding
import java.text.SimpleDateFormat
import java.util.*

class SunPositionActivity : ComponentActivity() {
    private lateinit var binding: ActivitySunPositionBinding
    private lateinit var sunPositionView: SunPositionView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySunPositionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize sun position view
        sunPositionView = SunPositionView(this)
        binding.sunPositionContainer.addView(sunPositionView)

        // Get data from intent or use default values
        val sunriseTime = intent.getStringExtra("sunrise") ?: "06:25 AM"
        val sunsetTime = intent.getStringExtra("sunset") ?: "08:30 PM"
        val dayLength = intent.getStringExtra("dayLength") ?: "13H 12M"
        val remainingDaylight = intent.getStringExtra("remainingDaylight") ?: "9H 22M"

        // Calculate current sun position based on current time
        val currentPosition = calculateCurrentSunPosition(sunriseTime, sunsetTime)

        // Update the view
        sunPositionView.updateTime(
            sunriseTime,
            sunsetTime,
            "06:00 PM"
        )
    }

    private fun calculateCurrentSunPosition(sunrise: String, sunset: String): Float {
        // Parse time strings to minutes since midnight
        val sunriseMinutes = parseTimeToMinutes(sunrise)
        val sunsetMinutes = parseTimeToMinutes(sunset)

        // Get current time in minutes
        val calendar = Calendar.getInstance()
        val currentMinutes = calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)

        // Handle edge cases
        if (currentMinutes <= sunriseMinutes) return 0f
        if (currentMinutes >= sunsetMinutes) return 1f

        // Calculate position as percentage between sunrise and sunset
        return (currentMinutes - sunriseMinutes).toFloat() / (sunsetMinutes - sunriseMinutes)
    }

    private fun parseTimeToMinutes(timeString: String): Int {
        // Parse formats like "06:25 AM" or "20:30"
        return try {
            if (timeString.contains("AM") || timeString.contains("PM")) {
                val formatter = SimpleDateFormat("hh:mm a", Locale.US)
                val date = formatter.parse(timeString)
                val calendar = Calendar.getInstance()
                calendar.time = date!!
                calendar.get(Calendar.HOUR_OF_DAY) * 60 + calendar.get(Calendar.MINUTE)
            } else {
                val parts = timeString.split(":")
                val hours = parts[0].toInt()
                val minutes = parts[1].toInt()
                hours * 60 + minutes
            }
        } catch (e: Exception) {
            // Default fallback
            6 * 60 // 6:00 AM
        }
    }
}