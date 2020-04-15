package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EncyclopediaDetailActivity : AppCompatActivity(), CoroutineScope {
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var userName: String? = ""
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
        textMoney = findViewById(R.id.textPricePlant)

        val idPlant: String = intent.getStringExtra("idPlant")

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.userName = prefs!!.getString("USER_ID", "")


        setPlant(idPlant)
        shopButton.setOnClickListener{
        buyPlant(idPlant)
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

    }
    fun buyPlant(plantName: String){
        launch{
            var actualUser = mainConnection!!.getUser(userName)
            var actualPlant = mainConnection!!.getPlant(plantName)
            actualUser?.setRainyCoins(actualUser.getRainyCoins() - actualPlant?.getMoney()!!)
            mainConnection!!.buyPlantToUser(actualUser!!, actualPlant!!)
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
            textMoney!!.text = actualPlant?.getMoney().toString()
            val drawableName : String? = actualPlant.getImagePlant()
            val resID: Int = resources.getIdentifier(drawableName, "drawable", applicationContext.packageName)
            imagePlant!!.setImageResource(resID)
        }
    }

}