package com.rainyteam.rainyseeds

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.greenhouse_layout.*
import kotlinx.android.synthetic.main.store_layout.*

class StoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_layout)

        buttonCloseStore.setOnClickListener{
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
        }

    }
}