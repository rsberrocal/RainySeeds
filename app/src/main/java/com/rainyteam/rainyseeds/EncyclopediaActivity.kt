package com.rainyteam.rainyseeds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EncyclopediaActivity : AppCompatActivity() {

    var plants: ArrayList<Plant>? = null
    var lista: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adaptador: AdapterPlant? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_layout)
        plants = ArrayList()
        plants?.add(Plant("Plant 1"));
        plants?.add(Plant("Plant 2"));
        plants?.add(Plant("Plant 3"));

        lista = findViewById(R.id.recyclerViewPlants)
        layoutManager = LinearLayoutManager(this)
        adaptador = AdapterPlant(plants!!)
        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador

    }
}
