package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
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
import java.lang.Exception
import kotlin.coroutines.CoroutineContext


private val NUM_PLANTS_PAGE = 9

class GreenhouseActivity : MusicAppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    private lateinit var mPager: ViewPager2
    private var numPages: Int = 1
    private var mutableList: MutableList<Plants>? = null
    var mBDD: FirebaseFirestore? = null

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
        setContentView(R.layout.greenhouse_layout)

        this.mainConnection = Connection()
        mBDD = mainConnection!!.mBDD()

        //val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)
        startService(timerService)

        val swMusic = findViewById<View>(R.id.swMusic) as Switch

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        val textSeeds: TextView = findViewById(R.id.textGoldenSeeds) as TextView

        swMusic.setOnCheckedChangeListener { _, isChecked ->
            if (swMusic.isChecked) {
                //startService(musicService)
                mBDD!!.collection("Users").document(user!!).update("music", true)
            } else {
                //stopService(musicService)
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
            textSeeds.text = auxUser.getRainyCoins().toString()
        }

    }

    fun boughtPlants() {
        launch {
            mutableList = mutableListOf()
            val auxList = mutableListOf<UserPlants>()
            mBDD!!.collection("User-Plants")
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        auxList.add(document.toObject(UserPlants::class.java))
                    }
                }.await()
            mBDD!!.collection("Plants")
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

            val pagerAdapter = PlantSlidePagerAdapter(this@GreenhouseActivity)

            numPages = Math.ceil(mutableList!!.size.toDouble() / NUM_PLANTS_PAGE.toDouble())
                .toInt() // round up division
            val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

            mPager.adapter = pagerAdapter
            dotsIndicator.setViewPager2(mPager)
        }
    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse(position)
    }

}