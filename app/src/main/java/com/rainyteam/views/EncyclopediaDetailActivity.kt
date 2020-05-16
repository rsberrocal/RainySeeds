package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class EncyclopediaDetailActivity : AppCompatActivity(), CoroutineScope {

    var userName: String? = ""
    var mainConnection: Connection? = null
    var textNamePlant: TextView? = null
    var textScientificName: TextView? = null
    var textBenefits: TextView? = null
    var textUses: TextView? = null
    var textWarnings: TextView? = null
    var textMoney: TextView? = null
    var imagePlant: ImageView? = null

    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

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

        val idPlant: String = intent.getStringExtra("idPlant")!!
        val statusPlant: Int = intent.getIntExtra("statusPlant", -2)

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, "")


        setPlant(idPlant, statusPlant)
        shopButton.setOnClickListener {
            buyPlant(idPlant)
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
        }

        launch {
            /** Delay para definir que no es navegacion al crear vista **/
            delay(500)
            prefs!!.edit().putBoolean("NAV", false).apply()
        }

    }

    override fun onStop() {
        super.onStop()
        //Se crea el intent para pararlo
        val musicService = Intent(this, MusicService::class.java)
        val isNav = prefs!!.getBoolean("NAV", false);
        //Se mira si es una navegacion, de no serla es un destroy de app, se apaga la musica
        if (!isNav) {
            //De ser un destroy se detiene
            stopService(musicService)
        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        if (!isTaskRoot) {
            prefs!!.edit().putBoolean("NAV", true).apply()
        }
        prefs!!.edit().putBoolean("NAV", false).apply()
    }

    //Viene de un destroy
    override fun onRestart() {
        super.onRestart()
        //Se crea el intent para iniciarlo
        val musicService = Intent(this, MusicService::class.java)
        var musicPlay = prefs!!.getBoolean("PLAY", false)
        //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
        launch {
            var auxUser: User = mainConnection!!.getUser(userName!!)!!
            if (auxUser.music && !musicPlay) {
                startService(musicService)
            }
        }
    }

    fun buyPlant(plantName: String) {
        launch {
            var actualUser = mainConnection!!.getUser(userName!!)
            var actualPlant = mainConnection!!.getPlant(plantName)
            if (actualUser!!.getRainyCoins() < actualPlant?.getMoney()!!) {
                Toast.makeText(
                    applicationContext,
                    "You don't have enough money!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                //se revive la planta
                if (actualPlant.isWither() || actualPlant.isDead()) {
                    actualUser.setRainyCoins(actualUser.getRainyCoins() - actualPlant.getMoney())
                    mainConnection!!.BDD.collection("Users")
                        .document(userName!!)
                        .update("rainyCoins", actualUser.getRainyCoins())

                    //actualizamos planta
                    mainConnection!!.BDD.collection("UserPlants")
                        .document(userName + "-" + plantName)
                        .update("status", actualPlant.getMoney())
                } else {
                    actualUser.setRainyCoins(actualUser.getRainyCoins() - actualPlant.getMoney())
                    mainConnection!!.buyPlantToUser(actualUser, actualPlant)
                }
            }
        }

    }

    fun setPlant(plant: String, statusPlant: Int) {
        launch {
            var actualPlant = mainConnection!!.getPlant(plant)
            textNamePlant!!.text = plant.replace("\\n", "\n")
            textScientificName!!.text = actualPlant?.getScientificName()!!.replace("\\n", "\n")
            textBenefits!!.text = actualPlant?.getBenefits()!!.replace("\\n", "\n")
            textUses!!.text = actualPlant?.getUses()!!.replace("\\n", "\n")
            textWarnings!!.text = actualPlant?.getPrecautions()!!.replace("\\n", "\n")
            textMoney!!.text = actualPlant?.getMoney().toString()
            val drawableName: String?
            val resID: Int
            if (statusPlant == -2) {
                Log.d("STATUS", "STATUS == -2 (DEAD)")
                drawableName =
                    "baw_" + actualPlant.getScientificName().toLowerCase().replace(" ", "_")
                resID = resources.getIdentifier(
                    drawableName,
                    "drawable",
                    applicationContext.packageName
                )
            } else {
                drawableName = actualPlant.getImagePlant()
                resID = resources.getIdentifier(
                    drawableName,
                    "drawable",
                    applicationContext.packageName
                )
            }

            imagePlant!!.setImageResource(resID)
        }
    }

}