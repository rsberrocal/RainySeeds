package com.rainyteam.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.main_water_layout.*


class MainWaterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_water_layout)
        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
        waterButton.setOnClickListener {
            val intent = Intent(this, IntroduceWaterActivity::class.java)
            startActivity(intent)
        }
    }
}
