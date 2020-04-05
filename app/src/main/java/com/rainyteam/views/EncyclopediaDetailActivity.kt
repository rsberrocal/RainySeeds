package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*

class EncyclopediaDetailActivity : AppCompatActivity() {

    private lateinit var plant: Plants
    var mainConnection: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_detail_layout)

        this.mainConnection = Connection()

        // plant =

        val textBenefits: TextView = findViewById(R.id.textBenefitsPlant)

        textBenefits.text = mainConnection!!.sendPlantBenefits().toString()

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }



    }


}