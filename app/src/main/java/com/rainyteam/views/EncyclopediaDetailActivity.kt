package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*

class EncyclopediaDetailActivity : AppCompatActivity() {

    var connection: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_detail_layout)

        this.connection = Connection()

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

        val textBenefits: TextView = findViewById(R.id.textBenefitsPlant) as TextView
        textBenefits.setOnClickListener {
            textBenefits.text = this.connection!!.sendPlantBenefits().toString()
        }
    }




}