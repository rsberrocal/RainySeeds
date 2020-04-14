package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.User
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
    private var mutableList: MutableList<Plants>? = null

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null

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

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        val user = prefs!!.getString("USER_ID", null)
        // user =

        launch{
            mutableList = mainConnection?.getAllPlants()
            numPages = (mutableList!!.size + NUM_PLANTS_PAGE - 1) / NUM_PLANTS_PAGE // round up division
        }

        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = PlantSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
        dotsIndicator.setViewPager2(mPager)
/*
        val textSeeds: TextView = findViewById(R.id.textGoldenSeeds) as TextView
        textSeeds.setOnClickListener {
            textSeeds.text = this.user.rainycoins.toString()
        }
*/
    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse()
    }

}