package com.example.weather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.astro.PlanetData
import com.example.weather.utils.AstroUtils
import com.example.whether.R
import com.example.whether.databinding.PlanetItemLayoutBinding

class PlanetAdapter: RecyclerView.Adapter<PlanetAdapter.PlanetHolder>() {
    private val planetsList = ArrayList<PlanetData>()

    class PlanetHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = PlanetItemLayoutBinding.bind(view)
        private val astroUtils = AstroUtils();

        @SuppressLint("SetTextI18n")
        fun bind(planet: PlanetData) = with(binding) {

            tvPlanet.text = planet.planet.en.take(5)
            tvDegree.text = "${"%.2f".format(planet.normDegree)}°"
            tvSingName.text = planet.zodiac_sign.name.en.take(5)

            ivPlanetIcon.setImageResource(
                astroUtils.getPlanetIcon(planet.planet.en)
            )

            ivSignIcon.setImageResource(
                astroUtils.getSignIcon(planet.zodiac_sign.name.en)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanetHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.planet_item_layout, parent, false)
        return PlanetHolder(view)
    }

    override fun getItemCount(): Int {
        return planetsList.size
    }


    override fun onBindViewHolder(holder: PlanetHolder, position: Int) {
        holder.bind(planetsList[position])
    }

    fun setPlanetsList(list: List<PlanetData>) {
        planetsList.clear()
        planetsList.addAll(list)
        notifyDataSetChanged()
    }
}