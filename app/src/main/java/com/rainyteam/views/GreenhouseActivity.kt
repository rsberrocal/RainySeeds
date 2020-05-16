package com.rainyteam.views

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.User
import com.rainyteam.model.UserPlants
import com.rainyteam.patterns.Observable
import com.rainyteam.patterns.Observer
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.greenhouse_layout.*
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext


private val NUM_PLANTS_PAGE = 9

class GreenhouseActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    private lateinit var mPager: ViewPager2
    private var numPages: Int = 1
    private var mutableList: MutableList<Plants>? = null
    var mBDD: FirebaseFirestore? = null

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
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        this.mainConnection = Connection()
        mBDD = mainConnection!!.mBDD()

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        //register receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("Timer"))

        val swMusic = findViewById<View>(R.id.swMusic) as Switch

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
            prefs!!.edit().putBoolean("NAV", true).apply()
        }

        val textSeeds: TextView = findViewById(R.id.textGoldenSeeds) as TextView

        swMusic.setOnCheckedChangeListener { _, isChecked ->
            if (swMusic.isChecked) {
                var musicPlay = prefs!!.getBoolean("PLAY", false)
                if (!musicPlay) {
                    Log.d("MUSIC", "STARTING ON CREATE")
                    startService(musicService)
                }
                mBDD!!.collection("Users").document(user!!).update("music", true)
            } else {
                stopService(musicService)
                mBDD!!.collection("Users").document(user!!).update("music", false)
            }
        }

        mPager = findViewById(R.id.pager)
        boughtPlants()
        launch {
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            if (auxUser.music) {
                swMusic.isChecked = true
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

    override fun onStop() {

        //Se crea el intent para pararlo
        val musicService = Intent(this, MusicService::class.java)

        val timerService = Intent(this, TimerService::class.java)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)

        val isNav = prefs!!.getBoolean("NAV", false);
        //Se mira si es una navegacion, de no serla es un destroy de app, se apaga la musica
        if (!isNav) {
            //De ser un destroy se detiene
            stopService(musicService)
            stopService(timerService)
        }
        super.onStop()
    }

    //Viene de un destroy
    override fun onRestart() {
        super.onRestart()
        Log.d("MUSIC", "ON RESTART GREENHOUSE")
        //Se crea el intent para iniciarlo
        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter("Timer"))

        var musicPlay = prefs!!.getBoolean("PLAY", false)
        //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
        launch {
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            if (auxUser.music && !musicPlay) {
                Log.d("MUSIC", "STARTING ON RESTART")
                startService(musicService)
            }
            startService(timerService)
        }
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

}