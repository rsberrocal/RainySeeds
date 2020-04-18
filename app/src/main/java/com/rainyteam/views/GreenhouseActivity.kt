package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.User
import com.rainyteam.model.UserPlants
import com.rainyteam.services.MusicService
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.greenhouse_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


private val NUM_PLANTS_PAGE = 9

class GreenhouseActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    private lateinit var mPager: ViewPager2
    private var numPages: Int = 1
    private var mutableList: MutableList<UserPlants>? = null
    var mBDD: FirebaseFirestore? = null

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = ""

    private var job: Job = Job()
    private var music: Intent? = null

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        music = Intent(this, MusicService::class.java)
        startService(music)

        mBDD = mainConnection!!.mBDD()

        val email = FirebaseAuth.getInstance().currentUser?.email.toString()
        launch {
            var auxUser: User = mainConnection!!.getUser(email)!!
            if (!auxUser.music) {
                startService(music)
            }
        }

        this.mainConnection = Connection()
        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        val textSeeds: TextView = findViewById<TextView>(R.id.textGoldenSeeds)
        val switch = findViewById<Switch>(R.id.Switch)

        val dataMusicOn = hashMapOf(
            "music" to true
        )
        val dataMusicOff = hashMapOf(
            "music" to false
        )

        switch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startService(music)
                mBDD!!.collection("Users").document(email).set(dataMusicOn)
            } else {
                stopService(music)
                mBDD!!.collection("Users").document(email).set(dataMusicOff)
            }
        }

        launch{
            textSeeds.text = mainConnection?.getUser(user!!)?.getRainyCoins().toString()
            mutableList = mainConnection?.getUserPlantsAlive(user!!)
            numPages = (mutableList!!.size + NUM_PLANTS_PAGE - 1) / NUM_PLANTS_PAGE // round up division
        }

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = PlantSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
        dotsIndicator.setViewPager2(mPager)
    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse()
    }

}