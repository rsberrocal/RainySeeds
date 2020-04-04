package com.rainyteam.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R


class EncyclopediaActivity : AppCompatActivity() {

    var plants: ArrayList<FragmentPlantGreenhouse>? = null
    var lista: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adaptador: AdapterPlant? = null

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var arrayList: ArrayList<Plant>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


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
/*
        plants = ArrayList()
        plants!!.add(FragmentPlantGreenhouse());
        plants!!.add(FragmentPlantGreenhouse());
        plants!!.add(FragmentPlantGreenhouse());

        lista = findViewById(R.id.recyclerViewPlants)
        layoutManager = LinearLayoutManager(this)
        adaptador = AdapterPlant(plants)
        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador
*/
    }

    private fun setDatainList(): ArrayList<Plant> {
        var items : ArrayList<Plant> = ArrayList()

        items.add(Plant(R.drawable.plant_cappsicum_frutescens, "Pimienta"))
        items.add(Plant(R.drawable.plant_filipendula_ulmaria, "Ulmaria"))
        items.add(Plant(R.drawable.plant_gentiana_lutea, "Gentiana"))
        items.add(Plant(R.drawable.plant_peonia_lactiflora, "Peonia"))

        return items
    }
}
