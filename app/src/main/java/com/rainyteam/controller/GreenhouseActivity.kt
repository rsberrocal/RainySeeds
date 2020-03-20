package com.rainyteam.controller

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.greenhouse_layout.*

private const val NUM_PAGES = 5

class GreenhouseActivity : AppCompatActivity() {

    private lateinit var mPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        buttonStore.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }

        mPager = findViewById(R.id.pager)
        val pagerAdapter = PlantSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter
    }

    private inner class PlantSlidePagerAdapter(fm: FragmentActivity) : FragmentStateAdapter(fm) {
        override fun getItemCount(): Int = NUM_PAGES

        override fun createFragment(position: Int): Fragment = FragmentPageGreenhouse()
    }

}