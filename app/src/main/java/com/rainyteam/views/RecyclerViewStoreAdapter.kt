package com.rainyteam.views

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import com.rainyteam.model.Plants

class RecyclerViewStoreAdapter (var context: Context, var listOfPlants: MutableList<Plants>) : RecyclerView.Adapter<RecyclerViewStoreAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val itemHolder = LayoutInflater.from(parent.context).inflate(R.layout.template_store_plant, parent, false)
        return ItemHolder(itemHolder)
    }

    override fun getItemCount(): Int {
        return listOfPlants.size
    }
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val plant: Plants = listOfPlants.get(position)

        val resources: Resources = context.resources
        val drawableName : String? = plant.getImagePlant()
        val resID: Int = resources.getIdentifier(drawableName, "drawable", context.packageName)
        holder.image.setImageResource(resID)
        holder.name.text = plant.getName()
        holder.image.setOnClickListener{
            val intent = Intent(holder.image.context, EncyclopediaDetailActivity::class.java)
            intent.putExtra("idPlant", plant.getName())
            holder.image.context.startActivity(intent)
        }
    }

    class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var image = itemView.findViewById<ImageView>(R.id.storePlant)
        var name = itemView.findViewById<TextView>(R.id.nameStorePlant)
    }
}