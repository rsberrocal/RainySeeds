package com.rainyteam.views

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R

class RecyclerViewAdapter (var context: Context, var arrayList: ArrayList<Plant>) : RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder>() {

    private lateinit var listOfPlants: ArrayList<Plant>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.template, parent, false)
        return ItemHolder(itemHolder)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        var plant: Plant = arrayList.get(position)
        holder.icons.setImageResource(plant.iconsChar!!)
        holder.alphas.text = plant.alphaChar
        holder.icons.setOnClickListener{
            Toast.makeText(context, plant.alphaChar, Toast.LENGTH_LONG).show()
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var icons = itemView.findViewById<ImageView>(R.id.plantImage)
        var alphas = itemView.findViewById<TextView>(R.id.alpha_text_view)
    }
}