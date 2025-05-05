package com.example.weather.utils

import com.example.whether.R

class AstroUtils {
    fun getPlanetIcon(planet: String): Int {
        when (planet.lowercase()) {
            "sun" -> R.drawable.ic_sun
            "moon" -> R.drawable.ic_moon
            "mercury" -> R.drawable.ic_mercury
            "venus" -> R.drawable.ic_venus
            "mars" -> R.drawable.ic_mars
            "jupiter" -> R.drawable.ic_jupiter
            "saturn" -> R.drawable.ic_saturn
            "uranus" -> R.drawable.ic_uranus
            "neptune" -> R.drawable.ic_neptune
            "pluto" -> R.drawable.ic_pluto

            "chiron" -> R.drawable.ic_chiron
            "ceres" -> R.drawable.ic_ceres
            "pallas" -> R.drawable.ic_pallas
            "juno" -> R.drawable.ic_juno
            "vesta" -> R.drawable.ic_vesta
            "lilith" -> R.drawable.ic_lilith

            "mean node" -> R.drawable.ic_mean_node
            "true node" -> R.drawable.ic_true_node

            "ascendant" -> R.drawable.ic_earth
            "descendant" -> R.drawable.ic_earth
            "mc" -> R.drawable.ic_mc
            "ic" -> R.drawable.ic_ic
        }
        return 0
    }

    fun getSignIcon(sign: String): Int {
        when (sign.lowercase()) {
            "aries" -> return R.drawable.ic_aries
            "taurus" -> return R.drawable.ic_taurus
            "gemini" -> return R.drawable.ic_gemini
            "cancer" -> return R.drawable.ic_cancer
            "leo" -> return R.drawable.ic_leo
            "virgo" -> return R.drawable.ic_virgo
            "libra" -> return R.drawable.ic_libra
            "scorpio" -> return R.drawable.ic_scorpio
            "sagittarius" -> return R.drawable.ic_sagittarius
            "capricorn" -> return R.drawable.ic_capricorn
            "aquarius" -> return R.drawable.ic_aquarius
            "pisces" -> return R.drawable.ic_pisces
        }
        return 0
    }
}
