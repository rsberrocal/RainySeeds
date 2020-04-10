package com.rainyteam.views

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.template.view.*

class AdapterPlant(items: ArrayList<FragmentPlantGreenhouse>?) :
    RecyclerView.Adapter<AdapterPlant.ViewHolder>() {

    var items: ArrayList<FragmentPlantGreenhouse>? = null
    var viewHolder: ViewHolder? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context)
            .inflate(R.layout.template_greenhouse_plant, parent, false)
        viewHolder = ViewHolder(view)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        holder?.btn.setOnClickListener {

            val intent = Intent(holder.view.context, EncyclopediaDetailActivity::class.java)
            intent.putExtra("PLANT_ID", "St John's Wort")
            holder.view.context.startActivity(intent)
        }
    }

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        var view = vista
        var nombre: TextView? = null
        var btn = vista.findViewById(R.id.plantImage) as ImageView

        init {
            // nombre = view.tvTemplate
        }
    }
}