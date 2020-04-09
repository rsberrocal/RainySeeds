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

    var textNamePlant: TextView? = null
    var textScientificName: TextView? = null
    var textBenefits: TextView? = null
    var textUses: TextView? = null
    var textWarnings: TextView? = null
    var textMoney: TextView? = null

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

        textNamePlant = findViewById(R.id.plantName)
        textScientificName = findViewById(R.id.scientificName)
        textBenefits = findViewById(R.id.textBenefitsPlant)
        textUses = findViewById(R.id.textUsesPlant)
        textWarnings = findViewById(R.id.textWarningsPlant)
        //textMoney = findViewById(R.id.textPricePlant)

        setPlant("St John's Wort")

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

    }

    fun setPlant(plant: String) {
        launch {
            var actualPlant = mainConnection!!.getPlant(plant)
            textNamePlant!!.text = "St John's Wort".replace("\\n", "\n")
            textScientificName!!.text = actualPlant?.getScientificName()!!.replace("\\n", "\n")
            textBenefits!!.text = actualPlant?.getBenefits()!!.replace("\\n", "\n")
            textUses!!.text = actualPlant?.getUses()!!.replace("\\n", "\n")
            textWarnings!!.text = actualPlant?.getPrecautions()!!.replace("\\n", "\n")
            //textMoney!!.text = actualPlant?.getMoneyCost().toString()
        }
    }

}