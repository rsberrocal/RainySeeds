package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import kotlinx.android.synthetic.main.store_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class StoreActivity : MusicAppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    /** DATABASE **/
    val database = FirebaseFirestore.getInstance()

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

        buyPlants()

        buttonCloseStore.setOnClickListener {
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
        }

    }

    fun buyPlants() {
        launch {
            mutableList = mutableListOf()
            /** GETTING ALIVE AND WITHER PLANTS TO EXCLUDE **/
            var excludeList = mutableListOf<UserPlants>()
            database.collection("User-Plants")
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        excludeList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()

            var deadList = mutableListOf<UserPlants>()
            database.collection("User-Plants")
                .whereEqualTo("status", -1)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        deadList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()


            database.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val actualPlant = document.toObject(Plants::class.java)
                        actualPlant.setName(document.id)
                        val isExclude = excludeList!!.firstOrNull { it.plantId == document.id }
                        if (isExclude == null) { //If is not alive or wither check first if is dead
                            val deadPlant = deadList.firstOrNull { it.plantId == document.id }
                            if (deadPlant != null) {
                                actualPlant.setImageName(
                                    "plant_" + actualPlant.getScientificName().toLowerCase().replace(
                                        " ",
                                        "_"
                                    )
                                )
                                actualPlant.setStatus(deadPlant.status)
                            } else {
                                actualPlant.setImageName(
                                    "baw_" + actualPlant.getScientificName().toLowerCase().replace(
                                        " ",
                                        "_"
                                    )
                                )
                                actualPlant.setStatus(-2)
                            }
                            mutableList!!.add(actualPlant)
                        }
                    }
                }.await()
            recyclerViewStoreAdapter = RecyclerViewStoreAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewStoreAdapter
        }
    }
}


