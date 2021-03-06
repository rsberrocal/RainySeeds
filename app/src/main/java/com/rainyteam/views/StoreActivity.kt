package com.rainyteam.views

import android.content.*
import android.os.Bundle
import android.util.Log
import android.widget.TextView
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
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.store_layout.*
import kotlinx.android.synthetic.main.store_layout.inputStoreSearch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

class StoreActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var mainConnection: Connection? = null

    /** DATABASE **/
    val database = FirebaseFirestore.getInstance()

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var user: String? = ""

    var firstNav = false

    private var recyclerView: RecyclerView? = null
    private var gridLayoutManager: GridLayoutManager? = null
    private var mutableList: MutableList<Plants>? = null
    private var recyclerViewStoreAdapter: RecyclerViewStoreAdapter? = null

    lateinit var textSeeds: TextView

    //reciver
    val onMoneyChange: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //actualizar datos
            Log.d("Timer", "Update en greenhouse")
            launch {
                var auxUser: User = mainConnection!!.getUser(user!!)!!
                textSeeds.text = auxUser.getRainyCoins().toString()
            }
        }
    }

    val onDeadPlant: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //actualizar datos
            Log.d("Timer", "Update en greenhouse")
            buyPlants()
        }
    }
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onMoneyChange)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(onDeadPlant)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_layout)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        this.mainConnection = Connection()

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(onMoneyChange, IntentFilter("Timer"))
        LocalBroadcastManager.getInstance(this).registerReceiver(onDeadPlant, IntentFilter("Dead"))


        prefs = getSharedPreferences(PREF_ID, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

        textSeeds = findViewById(R.id.textGoldenSeeds)

        recyclerView = findViewById(R.id.recyclerViewStore)
        gridLayoutManager =
            GridLayoutManager(applicationContext, 3, LinearLayoutManager.VERTICAL, false)
        recyclerView?.layoutManager = gridLayoutManager
        recyclerView?.setHasFixedSize(true)

        launch{
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            textSeeds.text = auxUser.getRainyCoins().toString()
        }

        buyPlants()

        buttonCloseStore.setOnClickListener {
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
        }
        inputStoreSearch.addTextChangedListener{
            search()
        }

    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val returnStore = Intent(this, GreenhouseActivity::class.java)
        startActivity(returnStore)
        finish()
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
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
                .whereEqualTo("status", -1)
                .whereEqualTo("userId", user)
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
            recyclerViewStoreAdapter = RecyclerViewStoreAdapter(applicationContext, mutableList!!)
            recyclerView?.adapter = recyclerViewStoreAdapter
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
        recyclerViewStoreAdapter = RecyclerViewStoreAdapter(applicationContext, aux!!)
        recyclerView?.adapter = recyclerViewStoreAdapter

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded store")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav){
            this.firstNav = true
        }else{
            Log.e("MUSIC", "************* foregrounded store")
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



