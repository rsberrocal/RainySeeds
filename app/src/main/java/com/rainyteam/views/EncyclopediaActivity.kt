package com.rainyteam.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.encyclopedia_layout.*


class EncyclopediaActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayList: ArrayList<Plant>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_layout)

        recyclerView = findViewById(R.id.recyclerViewPlants)
        gridLayoutManager = GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        arrayList = ArrayList()
        arrayList = setDatainList()
        recyclerViewAdapter = RecyclerViewAdapter(applicationContext, arrayList!!)
        recyclerView?.adapter = recyclerViewAdapter

        btnAllPlants.setOnClickListener {
            arrayList = setDatainList()
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, arrayList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
        btnBoughtPlants.setOnClickListener {
            arrayList = setBoughtPlants()
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, arrayList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
        btnToBuyPlants.setOnClickListener {
            arrayList = setToBuyPlants()
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, arrayList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }

    }

    private fun setDatainList(): ArrayList<Plant> {
        var items : ArrayList<Plant> = ArrayList()

        items.add(Plant(R.drawable.plant_cappsicum_frutescens, "Pimienta"))
        items.add(Plant(R.drawable.plant_filipendula_ulmaria, "Ulmaria"))
        items.add(Plant(R.drawable.plant_gentiana_lutea, "Gentiana"))
        items.add(Plant(R.drawable.plant_peonia_lactiflora, "Peonia"))

        return items
    }

    private fun setBoughtPlants(): ArrayList<Plant> {
        var items : ArrayList<Plant> = ArrayList()

        items.add(Plant(R.drawable.plant_gentiana_lutea, "Gentiana"))
        items.add(Plant(R.drawable.plant_peonia_lactiflora, "Peonia"))

        return items
    }

    private fun setToBuyPlants(): ArrayList<Plant> {
        var items : ArrayList<Plant> = ArrayList()

        items.add(Plant(R.drawable.plant_cappsicum_frutescens, "Pimienta"))
        items.add(Plant(R.drawable.plant_filipendula_ulmaria, "Ulmaria"))

        return items
    }
}
