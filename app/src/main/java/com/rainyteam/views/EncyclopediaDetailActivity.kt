package com.rainyteam.views

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
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
    var imagePlant: ImageView? = null

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
        imagePlant = findViewById(R.id.plantImageDetail)
        //textMoney = findViewById(R.id.textPricePlant)

        val idPlant: String = intent.getStringExtra("idPlant")


        setPlant(idPlant)

        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

    }

    fun setPlant(plant: String) {
        launch {
            var actualPlant = mainConnection!!.getPlant(plant)
            textNamePlant!!.text = plant.replace("\\n", "\n")
            textScientificName!!.text = actualPlant?.getScientificName()!!.replace("\\n", "\n")
            textBenefits!!.text = actualPlant?.getBenefits()!!.replace("\\n", "\n")
            textUses!!.text = actualPlant?.getUses()!!.replace("\\n", "\n")
            textWarnings!!.text = actualPlant?.getPrecautions()!!.replace("\\n", "\n")
            val drawableName : String? = actualPlant.getImagePlant()
            val resID: Int = resources.getIdentifier(drawableName, "drawable", applicationContext.packageName)
            imagePlant!!.setImageResource(resID)
            //textMoney!!.text = actualPlant?.getMoneyCost().toString()
        }
    }

}