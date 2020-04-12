package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import kotlinx.android.synthetic.main.store_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class StoreActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mutableList: MutableList<Plants>? = null
    private var recyclerViewStoreAdapter: RecyclerViewStoreAdapter? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_layout)

        this.mainConnection = Connection()

        recyclerView = findViewById(R.id.recyclerViewStore)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)
        launch {
            mutableList = mainConnection?.getAllPlants()
            recyclerViewStoreAdapter = RecyclerViewStoreAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewStoreAdapter
        }

        buttonCloseStore.setOnClickListener{
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
        }

    }
}