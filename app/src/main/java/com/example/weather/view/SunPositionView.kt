package com.example.weather.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import org.threeten.bp.Duration
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.min

class SunPositionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paints
    private val skyPaintDay = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#8AD4FF")
    }

    private val skyPaintNight = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#001862")
    }

    private val sunPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FFD700")
    }
    private val moonPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#D3D3D3")
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
    private var sunriseTime: LocalTime = LocalTime.of(6, 7)
    private var sunsetTime: LocalTime = LocalTime.of(12, 2)
    private var nowTime: LocalTime = LocalTime.of(20, 0)

    private var currentSunPosition: Float = 0f
    private var lengthOfDay: String = ""
    private var remainingDaylight: String = ""

    private val arcRect = RectF()

    private val formatter = DateTimeFormatter.ofPattern("HH:mm")

    fun updateTime(sunrise: String, sunset: String, now: LocalTime) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        sunriseTime = LocalTime.parse(sunrise, formatter)
        sunsetTime = LocalTime.parse(sunset, formatter)

        // Проверка: день или ночь
        if (now.isBefore(sunriseTime) || now.isAfter(sunsetTime)) {
            currentSunPosition = -1f // сигнализируем, что солнце не видно
        } else {
            val totalDuration = Duration.between(sunriseTime, sunsetTime).toMinutes().toFloat()
            val elapsed = Duration.between(sunriseTime, now).toMinutes().toFloat()
            currentSunPosition = elapsed / totalDuration
        }

        invalidate()
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
        val horizonHeight = height * 0.5f

        val sunriseMinute = sunriseTime.hour * 60f + sunriseTime.minute
        val sunsetMinute = sunsetTime.hour * 60f + sunsetTime.minute
        val nowMinute = nowTime.hour * 60f + nowTime.minute
        val totalDuration = sunsetMinute - sunriseMinute

        // Горизонт
        canvas.drawLine(0f, horizonHeight, width, horizonHeight, linePaint)

        // Отступы
        val paddingLeft = width / 1440f * sunriseMinute
        val paddingRight = width - width / 1440f * sunsetMinute

        // Ночная зона слева (парабола под горизонтом)
        val nightLeftH = sunriseMinute / 2f
        val nightLeftMaxHeight = (height * 0.5f) / 1440f * sunriseMinute

        val nightLeftY = { x: Float ->
            val normalizedX = (x - nightLeftH) / (sunriseMinute / 2f)
            horizonHeight + (-nightLeftMaxHeight * normalizedX * normalizedX + nightLeftMaxHeight)
        }

        val nightLeftX = { x: Float ->
            x / sunriseMinute * paddingLeft
        }

        val nightPathLeft = Path().apply {
            moveTo(nightLeftX(0f), nightLeftY(0f))
            val step = 1f
            var x = 0f
            while (x <= sunriseMinute) {
                lineTo(nightLeftX(x), nightLeftY(x))
                x += step
            }

            lineTo(nightLeftX(sunriseMinute), horizonHeight)
            lineTo(nightLeftX(0f), horizonHeight)

            close()
        }
        canvas.drawPath(nightPathLeft, skyPaintNight)

        // Ночная зона справа (парабола под горизонтом)
        val nightRightH = (sunsetMinute + 1440f) / 2f
        val nightRightMaxHeight = (height * 0.5f) / 1440f * (1440f - sunsetMinute)

        val nightRightY = { x: Float ->
            val normalizedX = (x - nightRightH) / ((1440f - sunsetMinute) / 2f)
            horizonHeight + (-nightRightMaxHeight * normalizedX * normalizedX + nightRightMaxHeight)
        }

        val nightRightX = { x: Float ->
            val rightDuration = 1440f - sunsetMinute
            val availableWidth = paddingRight
            width - availableWidth + (x - sunsetMinute) / rightDuration * availableWidth
        }

        val nightPathRight = Path().apply {
            moveTo(nightRightX(sunsetMinute), nightRightY(sunsetMinute))
            val step = 1f
            var x = sunsetMinute
            while (x <= 1440f) {
                lineTo(nightRightX(x), nightRightY(x))
                x += step
            }

            lineTo(nightRightX(1440f), horizonHeight)
            lineTo(nightRightX(sunsetMinute), horizonHeight)

            close()
        }
        canvas.drawPath(nightPathRight, skyPaintNight)

        // Парабола: вершина в зените
        val h = (sunriseMinute + sunsetMinute) / 2f
        val maxHeight = (height * 0.5f) / 1440f * (sunsetMinute - sunriseMinute)

        val sunY = { x: Float ->
            val normalizedX = (x - h) / (totalDuration / 2f)
            horizonHeight - (-maxHeight * normalizedX * normalizedX + maxHeight)
        }

        val sunX = { x: Float ->
            val availableWidth = width - paddingLeft - paddingRight
            paddingLeft + (x - sunriseMinute) / totalDuration * availableWidth
        }

        // Отрисовка дневной параболы
        val path = Path().apply {
            moveTo(sunX(sunriseMinute), sunY(sunriseMinute))
            val step = 0.5f
            var x = sunriseMinute + step
            while (x <= sunsetMinute) {
                lineTo(sunX(x), sunY(x))
                x += step
            }
            lineTo(sunX(sunsetMinute), sunY(sunsetMinute))
        }
        canvas.drawPath(path, skyPaintDay)

        // Солнце
        val sunRadius = min(width, height) * 0.03f
        val sunXPosition: Float
        val sunYPosition: Float
        val circlePaint: Paint
        when {
            nowMinute < sunriseMinute -> {
                // Левая ночная парабола
                sunXPosition = nightLeftX(nowMinute)
                sunYPosition = nightLeftY(nowMinute)
                circlePaint = moonPaint
            }

            nowMinute in sunriseMinute..sunsetMinute -> {
                // Дневная парабола
                sunXPosition = sunX(nowMinute)
                sunYPosition = sunY(nowMinute)
                circlePaint = sunPaint
            }

            else -> {
                // Правая ночная парабола
                sunXPosition = nightRightX(nowMinute)
                sunYPosition = nightRightY(nowMinute)
                circlePaint = moonPaint
            }

        }

        canvas.drawCircle(sunXPosition, sunYPosition, sunRadius, circlePaint)

        // Линии и подписи восхода и заката
        canvas.drawLine(sunX(sunriseMinute), horizonHeight, sunX(sunriseMinute), horizonHeight - height * 0.25f, linePaint)
        canvas.drawLine(sunX(sunsetMinute), horizonHeight, sunX(sunsetMinute), horizonHeight - height * 0.25f, linePaint)

        canvas.drawText("Sunrise", sunX(sunriseMinute), height * 0.22f, textPaint)
        canvas.drawText(sunriseTime.format(formatter), sunX(sunriseMinute), height * 0.28f, boldTextPaint)

        canvas.drawText("Sunset", sunX(sunsetMinute), height * 0.22f, textPaint)
        canvas.drawText(sunsetTime.format(formatter), sunX(sunsetMinute), height * 0.28f, boldTextPaint)
    }


}
