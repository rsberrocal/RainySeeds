package com.rainyteam.views

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import com.rainyteam.patterns.EndlessRecyclerViewScrollListener
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
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    // Store a member variable for the listener

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = ""

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

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

        recyclerView = findViewById(R.id.recyclerViewPlants)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                launch {
                    println("Testing")
                    var aux = mutableListOf<Plants>()
                    //aux = mainConnection!!.getAllPlants(mutableList!!.last())!!
                    //recyclerViewAdapter!!.notifyDataSetChanged()
                    scrollListener!!.resetState()
                }
            }
        }
        recyclerView!!.addOnScrollListener(scrollListener!!)
        recyclerView?.setHasFixedSize(true)
        launch {
            mutableList = mainConnection?.getAllPlants(null)
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }

        val btnFilterAll = findViewById(R.id.filterAll) as RadioButton
        val btnFilterBought = findViewById(R.id.filterBought) as RadioButton
        val btnFilterToBuy = findViewById(R.id.filterToBuy) as RadioButton

        btnFilterAll.isChecked = true

        btnFilterAll.setOnClickListener {
            launch {
                mutableList = mainConnection?.getAllPlants(null)
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }
        btnFilterBought.setOnClickListener {
            launch {
                var auxList: MutableList<UserPlants>? = mainConnection?.getUserPlantsAlive(user!!)
                mutableList = mutableListOf()
                for (userPlant in auxList!!) {
                    mutableList!!.add(mainConnection!!.getPlant(userPlant.plantId)!!)
                }
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }
        btnFilterToBuy.setOnClickListener {
            launch {
                mutableList = mainConnection?.getDeadPlants(user!!)
                recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
                recyclerView?.adapter = recyclerViewAdapter
            }
        }

    }
}
