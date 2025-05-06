package com.example.weather.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.dto.WeatherDetail
import com.example.whether.R

class WeatherDetailAdapter(private val details: List<WeatherDetail>) :
    RecyclerView.Adapter<WeatherDetailAdapter.DetailViewHolder>() {

    inner class DetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        val extraTextView: TextView = itemView.findViewById(R.id.extraTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather_detail, parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val detail = details[position]
        holder.titleTextView.text = detail.title
        holder.valueTextView.text = detail.value
        holder.extraTextView.text = detail.extra
    }

    override fun getItemCount(): Int = details.size
}
