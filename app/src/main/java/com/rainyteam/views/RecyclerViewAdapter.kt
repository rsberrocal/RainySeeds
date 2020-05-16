package com.rainyteam.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import com.rainyteam.model.Plants

class RecyclerViewAdapter (var context: Context, var listOfPlants: MutableList<Plants>) : RecyclerView.Adapter<RecyclerViewAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.template, parent, false)
        return ItemHolder(itemHolder)
    }

    override fun getItemCount(): Int {
        return listOfPlants.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val plant: Plants = listOfPlants.get(position)

        val PREF_NAME = "USER"
        var prefs: SharedPreferences? = context.getSharedPreferences(PREF_NAME,0)

        val resources: Resources = context.resources
        val drawableName : String? = plant.getImagePlant()
        val resID: Int = resources.getIdentifier(drawableName, "drawable", context.packageName)
        holder.image.setImageResource(resID)
        holder.name.text = plant.getName()
        holder.image.setOnClickListener{
            Log.d("Timer", "Nav to true")
            prefs!!.edit().putBoolean("NAV", true).apply()
            val intent = Intent(holder.image.context, EncyclopediaDetailActivity::class.java)
            intent.putExtra("idPlant", plant.getName())
            intent.putExtra("statusPlant", plant.getStatus())
            holder.image.context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image = itemView.findViewById<ImageView>(R.id.imagePlant)
        var name = itemView.findViewById<TextView>(R.id.namePlant)
    }
}