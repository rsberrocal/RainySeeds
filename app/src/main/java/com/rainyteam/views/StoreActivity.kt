package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.store_layout.*

class StoreActivity : AppCompatActivity() {

    var plants: ArrayList<FragmentPlantGreenhouse>? = null
    var lista: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adaptador: AdapterPlant? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_layout)

        plants = ArrayList()
        plants?.add(FragmentPlantGreenhouse());
        plants?.add(FragmentPlantGreenhouse());
        plants?.add(FragmentPlantGreenhouse());

        lista = findViewById(R.id.recyclerViewStore)
        layoutManager = LinearLayoutManager(this)
        adaptador = AdapterPlant(plants!!)
        lista?.layoutManager = layoutManager
        lista?.adapter = adaptador

        buttonCloseStore.setOnClickListener{
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
        }

    }
}