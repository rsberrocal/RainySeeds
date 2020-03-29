package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.rainyteam.controller.MainController
import com.rainyteam.controller.R
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import kotlinx.android.synthetic.main.greenhouse_layout.*

private const val NUM_PAGES = 5

class GreenhouseActivity : AppCompatActivity() {

    private lateinit var mPager: ViewPager2
    var mainController: MainController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)
        //Con esto se consigue el main controller
        this.mainController = intent.extras.getSerializable("MAIN_CONTROLLER") as MainController

        layoutSeeds.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)

        mPager = findViewById(R.id.pager)
        val pagerAdapter = PlantSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
        dotsIndicator.setViewPager2(mPager)

    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment =
            FragmentPageGreenhouse()
    }

}