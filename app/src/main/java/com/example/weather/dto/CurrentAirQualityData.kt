package com.example.weather.dto


data class CurrentAirQualityData(
    val time: String, // Время получения данных
    val interval: Int, // Интервал
    val pm10: Float?, // Частицы PM10
    val pm2_5: Float?, // Частицы PM2.5
    val carbon_monoxide: Float?, // Углеродный оксид
    val nitrogen_dioxide: Float?, // Диоксид азота
    val sulphur_dioxide: Float?, // Диоксид серы
    val ozone: Float?, // Озон
    val aerosol_optical_depth: Float?, // Оптическая плотность аэрозоля
    val dust: Float?, // Пыль
    val uv_index: Float?, // Ультрафиолетовый индекс
    val ammonia: Float?, // Аммиак
    val european_aqi: Int? // Европейский индекс качества воздуха (AQI)
)

