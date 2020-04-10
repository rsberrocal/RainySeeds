package com.rainyteam.views

import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import kotlinx.android.synthetic.main.encyclopedia_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class EncyclopediaActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mutableList: MutableList<Plants>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_layout)

        this.mainConnection = Connection()

        recyclerView = findViewById(R.id.recyclerViewPlants)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        launch {
            mutableList = mainConnection?.getPlants()
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }

        val btnFilterAll = findViewById<RadioButton>(R.id.filterAll)
        val btnFilterBought = findViewById<RadioButton>(R.id.filterBought)
        val btnFilterToBuy = findViewById<RadioButton>(R.id.filterToBuy)

        btnFilterAll.isChecked = true

        btnFilterAll.setOnClickListener {
            launch {
                mutableList = mainConnection?.getPlants()
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }
        btnFilterBought.setOnClickListener {
            launch {
                mutableList = mainConnection?.getPlants()
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }
        btnFilterToBuy.setOnClickListener {
            launch {
                mutableList = mainConnection?.getPlants()
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }

    }
}
