package com.rainyteam.views

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.User
import com.rainyteam.model.UserPlants
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.greenhouse_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


private val NUM_PLANTS_PAGE = 9

class GreenhouseActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var mainConnection: Connection? = null
    private lateinit var mPager: ViewPager2
    private var numPages: Int = 1
    private var mutableList: MutableList<Plants>? = null
    var mBDD: FirebaseFirestore? = null
    lateinit var textSeeds: TextView

    var firstNav = false

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var user: String? = ""
    private var job: Job = Job()

    //reciver
    val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            //actualizar datos
            Log.d("Timer", "Update en greenhouse")
            boughtPlants()
            launch {
                var auxUser: User = mainConnection!!.getUser(user!!)!!
                textSeeds.text = auxUser.getRainyCoins().toString()
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        this.mainConnection = Connection()
        mBDD = mainConnection!!.mBDD()

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("Timer"))

        val swMusic = findViewById<View>(R.id.swMusic) as SwitchCompat

        prefs = getSharedPreferences(PREF_ID, 0)
        //prefs!!.edit().putBoolean("PLAY", false).apply()
        this.user = prefs!!.getString(PREF_NAME, null)
        if (this.user == null) {
            val principal = Intent(this, LoginActivity::class.java)
            finish()
            startActivity(principal)
        }


        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
            prefs!!.edit().putBoolean("NAV", true).apply()
        }

        textSeeds = findViewById(R.id.textGoldenSeeds) as TextView

        swMusic.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                var musicPlay = prefs!!.getBoolean("PLAY", false)
                if (!musicPlay) {
                    Log.d("MUSIC", "STARTING ON CREATE")
                    startService(musicService)
                }
            } else {
                stopService(musicService)
            }
            mBDD!!.collection("Users").document(user!!).update("music", isChecked)
        }

        mPager = findViewById(R.id.pager)
        boughtPlants()
        launch {
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            swMusic.isChecked = auxUser.music
            var musicPlay = prefs!!.getBoolean("PLAY", false)
            if (auxUser.music && !musicPlay) {
                Log.d("MUSIC", "STARTING ON CREATE")
                startService(musicService)
            }
            startService(timerService)
            textSeeds.text = auxUser.getRainyCoins().toString()
            /** Delay para definir que no es navegacion al crear vista **/
            delay(1000)
            prefs!!.edit().putBoolean("NAV", false).apply()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (!isTaskRoot) {
            prefs!!.edit().putBoolean("NAV", true).apply()
        }
        prefs!!.edit().putBoolean("NAV", false).apply()
    }


    fun boughtPlants() {
        launch {
            mutableList = mutableListOf()
            var count = 0
            val auxList = mutableListOf<UserPlants>()
            mBDD!!.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        count++
                    }
                }.await()
            val pagerAdapter = PlantSlidePagerAdapter(this@GreenhouseActivity)

            numPages = Math.ceil(count / NUM_PLANTS_PAGE.toDouble())
                .toInt() // round up division
            val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

            mPager.adapter = pagerAdapter
            dotsIndicator.setViewPager2(mPager)
        }
    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) :
        FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse(position)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded greenhouse")

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
            Log.e("MUSIC", "************* foregrounded greenhouse")
            // App in foreground
            //Se crea el intent para iniciarlo
            val musicService = Intent(this, MusicService::class.java)
            val timerService = Intent(this, TimerService::class.java)
            //var musicPlay = prefs!!.getBoolean("PLAY", false)
            //val isNav = prefs!!.getBoolean("NAV", false);
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

}