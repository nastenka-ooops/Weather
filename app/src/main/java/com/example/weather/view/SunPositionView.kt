package com.example.weather.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.min
import kotlin.math.pow

class SunPositionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paints
    private val skyPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#8AD4FF")
    }

    private val nightPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#001862")
    }

    private val sunPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD700")
    }

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#D3D3D3")
        strokeWidth = 2f
        isAntiAlias = true
    }

    private val textPaint = Paint().apply {
        color = Color.parseColor("#737373")
        textSize = 36f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }

    private val boldTextPaint = Paint().apply {
        color = Color.parseColor("#444444")
        textSize = 40f
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    // Values
    private var sunriseTime: LocalTime = LocalTime.of(12, 0)
    private var sunsetTime: LocalTime = LocalTime.of(20, 0)
    private var nowTime: LocalTime = LocalTime.of(13, 0)

    private var currentSunPosition: Float = 0f
    private var lengthOfDay: String = ""
    private var remainingDaylight: String = ""

    private val arcRect = RectF()

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    fun updateTime(sunrise: String, sunset: String, now: String) {
        try {
            sunriseTime = LocalTime.parse(sunrise, formatter)
            sunsetTime = LocalTime.parse(sunset, formatter)
            nowTime = LocalTime.parse(now, formatter)

            val totalDuration = Duration.between(sunriseTime, sunsetTime).toMinutes().coerceAtLeast(1)
            val passed = Duration.between(sunriseTime, nowTime).toMinutes().coerceIn(0, totalDuration)

            currentSunPosition = passed.toFloat() / totalDuration
            lengthOfDay = formatDuration(totalDuration)
            remainingDaylight = formatDuration((totalDuration - passed).coerceAtLeast(0))

            invalidate()
        } catch (e: Exception) {
            // Handle invalid time format
        }
    }

    private fun formatDuration(minutes: Long): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return "${hours}H ${mins}M"
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val padding = width * 0.08f
        val horizonHeight = height * 0.7f

        val sunriseHour = sunriseTime.hour + sunriseTime.minute / 60f
        val sunsetHour = sunsetTime.hour + sunsetTime.minute / 60f
        val nowHour = nowTime.hour + nowTime.minute / 60f

        val totalDuration = sunsetHour - sunriseHour

        // Horizon line
        canvas.drawLine(padding, horizonHeight, width - padding, horizonHeight, linePaint)
        canvas.drawText("Horizon", width - padding - 60f, horizonHeight + 30f, textPaint)


        // Night (left & right)
        val nightPath = Path()
        nightPath.moveTo(0f, height * 0.7f)
        nightPath.lineTo(padding, height * 0.7f)
        nightPath.lineTo(padding, 0f)
        nightPath.lineTo(0f, 0f)
        nightPath.close()
        canvas.drawPath(nightPath, nightPaint)

        val nightPath2 = Path()
        nightPath2.moveTo(width - padding, height * 0.7f)
        nightPath2.lineTo(width, height * 0.7f)
        nightPath2.lineTo(width, 0f)
        nightPath2.lineTo(width - padding, 0f)
        nightPath2.close()
        canvas.drawPath(nightPath2, nightPaint)

        // Sun position (parabola)
        val a = -1f / (totalDuration / 2f).pow(2) // Параметр для параболы
        val h = (sunriseHour + sunsetHour) / 2f // Вершина параболы — зенит

        val sunY = { x: Float ->
            val y = a * (x - h).pow(2)
            height * 0.4f - y * (height * 0.3f) // Масштабируем по оси Y
        }

        val sunX = { x: Float -> padding + (x - sunriseHour) / totalDuration * (width - 2 * padding) }

        // Построение параболы
        val path = Path()
        path.moveTo(sunX(sunriseHour), sunY(sunriseHour))
        for (x in sunriseHour.toInt()..sunsetHour.toInt()) {
            path.lineTo(sunX(x.toFloat()), sunY(x.toFloat()))
        }
        canvas.drawPath(path, skyPaint)

        // Отрисовка солнца
        val sunXPosition = sunX(nowHour)
        val sunYPosition = sunY(nowHour)
        val sunRadius = min(width, height) * 0.03f
        canvas.drawCircle(sunXPosition, sunYPosition, sunRadius, sunPaint)

        // Sunrise & Sunset lines and labels
        canvas.drawLine(sunX(sunriseHour), height * 0.7f, sunX(sunriseHour), height * 0.7f - height * 0.25f, linePaint)
        canvas.drawLine(sunX(sunsetHour), height * 0.7f, sunX(sunsetHour), height * 0.7f - height * 0.25f, linePaint)

        canvas.drawText("Sunrise", sunX(sunriseHour), height * 0.22f, textPaint)
        canvas.drawText(sunriseTime.format(formatter), sunX(sunriseHour), height * 0.28f, boldTextPaint)

        canvas.drawText("Sunset", sunX(sunsetHour), height * 0.22f, textPaint)
        canvas.drawText(sunsetTime.format(formatter), sunX(sunsetHour), height * 0.28f, boldTextPaint)

        // Info text
        val infoY = height * 0.78f
        val infoLabelY = height * 0.85f

        canvas.drawText("Length of day:", width * 0.28f, infoY, textPaint)
        canvas.drawText(lengthOfDay, width * 0.28f, infoLabelY, boldTextPaint)

        canvas.drawText("Remaining daylight:", width * 0.72f, infoY, textPaint)
        canvas.drawText(remainingDaylight, width * 0.72f, infoLabelY, boldTextPaint)
    }
}
