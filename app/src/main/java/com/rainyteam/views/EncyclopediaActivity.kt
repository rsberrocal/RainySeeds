package com.rainyteam.views

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.User
import com.rainyteam.model.UserPlants
import com.rainyteam.patterns.EndlessRecyclerViewScrollListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


class EncyclopediaActivity : MusicAppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    /** DATABASE **/
    val database = FirebaseFirestore.getInstance()

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mutableList: MutableList<Plants>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    // Store a member variable for the listener

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
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

        prefs = getSharedPreferences(PREF_ID, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

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

        val btnFilterAll = findViewById(R.id.filterAll) as RadioButton
        val btnFilterBought = findViewById(R.id.filterBought) as RadioButton
        val btnFilterToBuy = findViewById(R.id.filterToBuy) as RadioButton

        btnFilterAll.isChecked = true
        /** GET ALL PLANTS FROM DATABASE, FIRST WE GET USER PLANTS AND LATER THE OTHER PLANTS **/
        allPlants()

        btnFilterAll.setOnClickListener {
            allPlants()
        }
        btnFilterBought.setOnClickListener {
            boughtPlants()
        }
        btnFilterToBuy.setOnClickListener {
            buyPlants()
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
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
    }

    fun boughtPlants() {
        launch {
            mutableList = mutableListOf()
            val auxList = mutableListOf<UserPlants>()
            database.collection("User-Plants")
                .whereGreaterThan("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        auxList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()
            database.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val actualPlant = document.toObject(Plants::class.java)
                        val userPlant = auxList!!.firstOrNull { it.plantId == document.id }
                        if (userPlant != null) {
                            actualPlant.setName(document.id)
                            actualPlant.setImageName(
                                "plant_" + actualPlant.getScientificName().toLowerCase().replace(
                                    " ",
                                    "_"
                                )
                            )
                            actualPlant.setStatus(userPlant.status)
                            mutableList!!.add(actualPlant)
                        }
                    }
                }.await()
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
    }

    fun allPlants() {
        launch {
            mutableList = mutableListOf()
            var auxList = mainConnection!!.getUserPlantsId(user!!)
            var boughtPlants: MutableList<Plants>? = mutableListOf()
            var buyPlants: MutableList<Plants>? = mutableListOf()

            database.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val actualPlant = document.toObject(Plants::class.java)
                        actualPlant.setName(document.id)
                        val userPlant: UserPlants? =
                            auxList!!.firstOrNull { it.plantId == document.id }
                        if (userPlant != null) {
                            actualPlant.setStatus(userPlant.status)
                            actualPlant.setImageName(
                                "plant_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                    " ",
                                    "_"
                                )
                            )
                            boughtPlants!!.add(actualPlant)
                        } else {
                            actualPlant.setStatus(-2)
                            actualPlant.setImageName(
                                "baw_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                    " ",
                                    "_"
                                )
                            )
                            buyPlants!!.add(actualPlant)
                        }
                    }
                    mutableList = mutableList?.let { buyPlants!!.plus(it).toMutableList() }
                    mutableList = mutableList?.let { boughtPlants!!.plus(it).toMutableList() }
                }.await()

            //mutableList = mainConnection?.getAllPlants(null)
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
    }
}
