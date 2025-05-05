package com.example.weather.utils

import com.example.whether.R

class AstroUtils {
    fun getPlanetIcon(planet: String): Int {
        when (planet.lowercase()) {
            "sun" -> return R.drawable.ic_sun
            "moon" -> return R.drawable.ic_moon
            "mercury" -> return R.drawable.ic_mercury
            "venus" -> return R.drawable.ic_venus
            "mars" -> return R.drawable.ic_mars
            "jupiter" -> return R.drawable.ic_jupiter
            "saturn" -> return R.drawable.ic_saturn
            "uranus" -> return R.drawable.ic_uranus
            "neptune" -> return R.drawable.ic_neptune
            "pluto" -> return R.drawable.ic_pluto
            "chiron" -> return R.drawable.ic_chiron
            "ceres" -> return R.drawable.ic_ceres
            "pallas" -> return R.drawable.ic_pallas
            "juno" -> return R.drawable.ic_juno
            "vesta" -> return R.drawable.ic_vesta
            "lilith" -> return R.drawable.ic_lilith
            "mean node" -> return R.drawable.ic_mean_node
            "true node" -> return R.drawable.ic_true_node
            "ascendant" -> return R.drawable.ic_earth
            "descendant" -> return R.drawable.ic_earth
            "mc" -> return R.drawable.ic_mc
            "ic" -> return R.drawable.ic_ic
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
