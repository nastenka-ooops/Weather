import com.example.weather.dto.AirQualityResponse
import com.example.weather.dto.LocationResponse
import com.example.weather.dto.WeatherResponse

// Класс для хранения полных данных о погоде в локации
data class LocationWeatherData(
    val location: LocationResponse,
    val weatherData: WeatherResponse? = null,
    val airQualityData: AirQualityResponse? = null,
    val lastUpdated: Long = System.currentTimeMillis()
)