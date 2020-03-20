package com.rainyteam.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.model.Plant
import kotlinx.android.synthetic.main.template.view.*

class AdapterPlant (items:ArrayList<Plant>): RecyclerView.Adapter<AdapterPlant.ViewHolder>() {

    var items: ArrayList<Plant>? = null
    var viewHolder: ViewHolder? = null

    init {
        this.items = items
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.template_greenhouse_plant, parent, false)
        viewHolder = ViewHolder(view)
        return viewHolder!!
    }

    override fun getItemCount(): Int {
        return this.items?.count()!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items?.get(position)
        holder.nombre?.text = item?.name

    }

    class ViewHolder(vista: View): RecyclerView.ViewHolder(vista){
        var view = vista
        var nombre:TextView? = null

        init {
            nombre = view.tvTemplate
        }
    }
}