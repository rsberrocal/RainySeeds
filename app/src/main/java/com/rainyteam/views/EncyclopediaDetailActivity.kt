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

    var mainConnection: Connection? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_detail_layout)

        this.mainConnection = Connection()


        val textNamePlant: TextView = findViewById(R.id.plantName)

        val textScientificName: TextView = findViewById(R.id.scientificName)

        val textBenefits: TextView = findViewById(R.id.textBenefitsPlant)
        mainConnection!!.getPlantBenefits().collection("Plants").document("St John's Wort").get().addOnSuccessListener {
            document -> var plant = document.toObject(Plants::class.java)
            if (plant != null) {
                textBenefits.text = plant.getBenefits()!!.replace("\\n", "\n")
            }
        }

        val textUses: TextView = findViewById(R.id.textUsesPlant)

        val textWarnings: TextView = findViewById(R.id.textWarningsPlant)

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

    }


}