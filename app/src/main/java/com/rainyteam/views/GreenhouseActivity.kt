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
import com.rainyteam.model.User
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.greenhouse_layout.*


private val NUM_PLANTS_PAGE = 9

class GreenhouseActivity : AppCompatActivity() {
    private lateinit var mPager: ViewPager2

    private var numPages: Int = 1
    private var listPlants: ArrayList<String> = ArrayList()
    private lateinit var user: User

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        prefs = getSharedPreferences(PREF_NAME, 0)
        val user = prefs!!.getString("USER_ID", null)
        // user =
        //listPlants = user.getGreenhousePlants()
        numPages = (listPlants.size + NUM_PLANTS_PAGE - 1) / NUM_PLANTS_PAGE // round up division

        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = PlantSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
        dotsIndicator.setViewPager2(mPager)

        val textSeeds: TextView = findViewById(R.id.textGoldenSeeds) as TextView
        textSeeds.setOnClickListener {
            textSeeds.text = this.user.rainycoins.toString()
        }

    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = numPages

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse()
    }

}