import android.content.Context
import android.content.SharedPreferences

class ChosenUnits(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AppPreferences"
        private const val TEMP_UNIT = "temp_unit"
        private const val WIND_SPEED_UNIT = "wind_speed_unit"
        private const val PRESSURE_UNIT = "pressure_unit"
    }

    var temperatureUnit: String
        get() = sharedPreferences.getString("temp_unit", "celsius") ?: "celsius"
        set(value) = sharedPreferences.edit().putString("temp_unit", value).apply()

    var windSpeedUnit: String
        get() = sharedPreferences.getString("wind_speed_unit", "ms") ?: "ms"
        set(value) = sharedPreferences.edit().putString("wind_speed_unit", value).apply()

    var pressureUnit: String
        get() = sharedPreferences.getString("pressure_unit", "mmhg") ?: "mmhg"
        set(value) = sharedPreferences.edit().putString("pressure_unit", value).apply()


    public fun getApiTemperatureUnit(): String {
        return when (sharedPreferences.getString("temp_unit", "celsius")) {
            "°C" -> "celsius"
            "°F" -> "fahrenheit"
            else -> "celsius"
        }
    }

    public fun getApiWindSpeedUnit(): String {
        return when (sharedPreferences.getString("wind_speed_unit", "ms")) {
            "m/s" -> "ms"
            "km/h" -> "kmh"
            "mph" -> "mph"
            "knots" -> "kn"
            else -> "ms"
        }
    }

}