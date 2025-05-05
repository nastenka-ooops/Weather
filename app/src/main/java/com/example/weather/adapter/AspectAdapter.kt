package com.example.weather.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.astro.AspectData
import com.example.weather.utils.AstroUtils
import com.example.whether.R
import com.example.whether.databinding.AspectItemLayoutBinding

class AspectAdapter : RecyclerView.Adapter<AspectAdapter.AspectHolder>() {
    private val aspectsList = ArrayList<AspectData>()

    class AspectHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = AspectItemLayoutBinding.bind(view)
        private val astroUtils = AstroUtils();

        @SuppressLint("SetTextI18n")
        fun bind(aspect: AspectData) = with(binding) {

            tvFirstPlanet.text = aspect.planet_1.en
            tvSecondPlanet.text = aspect.planet_2.en
            tvAspect.text = aspect.aspect.en

            ivFirstPlanetIcon.setImageResource(
                astroUtils.getPlanetIcon(aspect.planet_1.en)
            )
            ivSecondPlanetIcon.setImageResource(
                astroUtils.getPlanetIcon(aspect.planet_2.en)
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AspectHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.aspect_item_layout, parent, false)
        return AspectHolder(view)
    }

    override fun getItemCount(): Int {
        return aspectsList.size
    }


    override fun onBindViewHolder(holder: AspectHolder, position: Int) {
        holder.bind(aspectsList[position])
    }

    fun setAspectsList(list: List<AspectData>) {
        aspectsList.clear()
        aspectsList.addAll(list)
        notifyDataSetChanged()
    }
}