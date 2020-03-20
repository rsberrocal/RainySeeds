package com.rainyteam.rainyseeds

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.greenhouse_layout.*

class GreenhouseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.greenhouse_layout)

        buttonStore.setOnClickListener{
            val intent = Intent(this, StoreActivity::class.java)
            startActivity(intent)
        }



    }
}