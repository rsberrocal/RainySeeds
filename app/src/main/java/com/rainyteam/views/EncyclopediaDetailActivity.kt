package com.rainyteam.views

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.encyclopedia_detail_layout.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class EncyclopediaDetailActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var userName: String? = ""
    var mainConnection: Connection? = null
    var textNamePlant: TextView? = null
    var textScientificName: TextView? = null
    var textBenefits: TextView? = null
    var textUses: TextView? = null
    var textWarnings: TextView? = null
    var textMoney: TextView? = null
    var imagePlant: ImageView? = null
    var shopButton : LinearLayout? = null

    var drawableName: String? = null
    var resID: Int? = null

    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var firstNav = false

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encyclopedia_detail_layout)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        this.mainConnection = Connection()

        textNamePlant = findViewById(R.id.plantName)
        textScientificName = findViewById(R.id.scientificName)
        textBenefits = findViewById(R.id.textBenefitsPlant)
        textUses = findViewById(R.id.textUsesPlant)
        textWarnings = findViewById(R.id.textWarningsPlant)
        imagePlant = findViewById(R.id.plantImageDetail)
        textMoney = findViewById(R.id.textPricePlant)
        shopButton = findViewById(R.id.shopButton)

        val idPlant: String = intent.getStringExtra("idPlant")!!
        val statusPlant: Int = intent.getIntExtra("statusPlant", -2)

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, "")


        setPlant(idPlant, statusPlant)
        if (statusPlant >= 0) {
            shopButton!!.setVisibility(View.INVISIBLE)
        }
        shopButton!!.setOnClickListener {
            buyPlant(idPlant)
        }
        btnBack.setOnClickListener {
            val intent = Intent(this, EncyclopediaActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
            prefs!!.edit().putBoolean("NAV", true).apply()
        }

        launch {
            /** Delay para definir que no es navegacion al crear vista **/
            delay(1000)
            prefs!!.edit().putBoolean("NAV", false).apply()
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
                if (actualPlant.isDead()) {
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
                shopButton!!.setVisibility(View.INVISIBLE)
                drawableName = actualPlant.getLiveImagePlant()
                resID = resources.getIdentifier(
                    drawableName,
                    "drawable",
                    applicationContext.packageName
                )
                imagePlant!!.setImageResource(resID!!)
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
            if (statusPlant == -2) {
                Log.d("STATUS", "STATUS == -2 (NO COMPRADA)")
                drawableName = actualPlant.getBawImagePlant()
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

            imagePlant!!.setImageResource(resID!!)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded Enciclopedia detail")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav){
            this.firstNav = true
        }else{
            Log.e("MUSIC", "************* foregrounded Enciclopedia detail")
            // App in foreground
            //Se crea el intent para iniciarlo
            val musicService = Intent(this, MusicService::class.java)
            val timerService = Intent(this, TimerService::class.java)
            //var musicPlay = prefs!!.getBoolean("PLAY", false)
            //val isNav = prefs!!.getBoolean("NAV", false);
            //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
            launch {
                var auxUser: User = mainConnection!!.getUser(userName!!)!!
                if (auxUser.music) {
                    Log.d("MUSIC", "STARTING ON RESTART")
                    startService(musicService)
                }
                startService(timerService)
            }
        }
    }

}