package com.rainyteam.views

import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.encyclopedia_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


class EncyclopediaActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var mainConnection: Connection? = null

    /** DATABASE **/
    val database = FirebaseFirestore.getInstance()

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mutableList: MutableList<Plants>? = null
    private var recyclerViewAdapter: RecyclerViewAdapter? = null
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    // Store a member variable for the listener

    var firstNav = false

    //reciver
    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //actualizar datos
            Log.d("Timer", "Update en diccionary")
            when (actualStatus) {
                0 -> allPlants()
                1 -> boughtPlants()
                2 -> buyPlants()
            }
        }
    }

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    //status de los filtros
    //0 All, 1 Bought, 2 To Buy
    var actualStatus = 0

    var user: String? = ""

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_layout)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        this.mainConnection = Connection()

        //register receiver
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter("Dead"))

        prefs = getSharedPreferences(PREF_ID, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

        recyclerView = findViewById(R.id.recyclerViewPlants)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)

        val btnFilterAll = findViewById(R.id.filterAll) as RadioButton
        val btnFilterBought = findViewById(R.id.filterBought) as RadioButton
        val btnFilterToBuy = findViewById(R.id.filterToBuy) as RadioButton

        btnFilterAll.isChecked = true
        /** GET ALL PLANTS FROM DATABASE, FIRST WE GET USER PLANTS AND LATER THE OTHER PLANTS **/
        allPlants()

        btnFilterAll.setOnClickListener {
            allPlants()
            actualStatus = 0;
        }
        btnFilterBought.setOnClickListener {
            boughtPlants()
            actualStatus = 1;
        }
        btnFilterToBuy.setOnClickListener {
            buyPlants()
            actualStatus = 2;
        }
        inputStoreSearch.addTextChangedListener {
            search()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnEncyclopediaActivity = Intent(this, GreenhouseActivity::class.java)
        startActivity(returnEncyclopediaActivity)
        finish()
        overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_stop)
    }

    fun buyPlants() {
        launch {
            mutableList = mutableListOf()
            /** GETTING ALIVE AND WITHER PLANTS TO EXCLUDE **/
            var excludeList = mutableListOf<UserPlants>()
            database.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        excludeList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()

            var deadList = mutableListOf<UserPlants>()
            database.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereEqualTo("status", -1)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        deadList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()


            database.collection("Plants")
                .orderBy("money")
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
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        auxList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()
            database.collection("Plants")
                .orderBy("money")
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
            var auxList = mutableListOf<UserPlants>()
            try {
                auxList = mainConnection!!.getUserPlantsId(user!!)!!
            } catch (ex: Exception) {
                Log.d("Connection", ex.message)
            }

            var boughtPlants: MutableList<Plants>? = mutableListOf()
            var buyPlants: MutableList<Plants>? = mutableListOf()

            database.collection("Plants")
                .orderBy("money")
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
            recyclerViewAdapter = RecyclerViewAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewAdapter
        }
    }

    fun search() {
        var strToFind = inputStoreSearch.text.toString()
        var lenToFind: Int = strToFind.length
        Log.d("Search", "Length " + lenToFind)
        var aux: MutableList<Plants>? = mutableListOf()
        for (plant in mutableList!!) {
            Log.d("Search", "Plant name " + plant.getName())
            Log.d("Search", "Plant name len" + plant.getName().length)
            if (plant.getName().length > lenToFind - 1) {
                var compareStr = plant.getName().substring(0, lenToFind)
                if (lenToFind > 1) {
                    if (levenshtein(compareStr, strToFind) < 2) {
                        aux!!.add(plant)
                    }
                } else {
                    if (compareStr == strToFind) {
                        aux!!.add(plant)
                    }
                }
            }
        }
        recyclerViewAdapter = RecyclerViewAdapter(this@EncyclopediaActivity, aux!!)
        recyclerView?.adapter = recyclerViewAdapter

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded Enciclopedia")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav) {
            this.firstNav = true
        } else {
            Log.e("MUSIC", "************* foregrounded Enciclopedia")
            // App in foreground
            //Se crea el intent para iniciarlo
            val musicService = Intent(this, MusicService::class.java)
            val timerService = Intent(this, TimerService::class.java)
            //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
            launch {
                var auxUser: User = mainConnection!!.getUser(user!!)!!
                if (auxUser.music) {
                    Log.d("MUSIC", "STARTING ON RESTART")
                    startService(musicService)
                }
                startService(timerService)
            }
        }
    }
    fun levenshtein(lhs : String, rhs : String) : Int {
        val lhsLength = lhs.length
        val rhsLength = rhs.length

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1..rhsLength-1) {
            newCost[0] = i

            for (j in 1..lhsLength-1) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }
}
