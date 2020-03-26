package com.rainyteam.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.introduce_water_layout.*

class IntroduceWaterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introduce_water_layout)
        falseBackButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }
    }
}
