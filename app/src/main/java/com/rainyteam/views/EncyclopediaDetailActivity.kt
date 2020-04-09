package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EncyclopediaDetailActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_detail_layout)

        this.mainConnection = Connection()


        val textNamePlant: TextView = findViewById(R.id.plantName)

        val textScientificName: TextView = findViewById(R.id.scientificName)

        val textBenefits: TextView = findViewById(R.id.textBenefitsPlant)
        mainConnection!!.getPlantBenefits().collection("Plants").document("St John's Wort").get()
            .addOnSuccessListener { document ->
                var plant = document.toObject(Plants::class.java)
                if (plant != null) {
                    textBenefits.text = plant.getBenefits()!!.replace("\\n", "\n")
                }
            }
        setPlant("St John's Wort")
        val textUses: TextView = findViewById(R.id.textUsesPlant)

        val textWarnings: TextView = findViewById(R.id.textWarningsPlant)

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

    }

    fun setPlant(plant: String) {
         launch {
            var actualPlant = mainConnection!!.getPlant(plant);
        }
    }


}