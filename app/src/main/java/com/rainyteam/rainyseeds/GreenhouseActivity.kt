package com.rainyteam.rainyseeds

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.greenhouse_layout.*

class GreenhouseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        val intent = findViewById(R.id.buttonStore) as ImageView
        intent.setOnClickListener {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.store_layout)
        }

    }
}