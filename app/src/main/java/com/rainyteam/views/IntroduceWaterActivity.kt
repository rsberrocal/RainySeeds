package com.rainyteam.views

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rainyteam.controller.R
import com.rainyteam.model.*
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.introduce_water_layout.*
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class IntroduceWaterActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var user: String? = ""

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.introduce_water_layout)
        falseBackButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }

        this.mainConnection = Connection()
        prefs = getSharedPreferences(PREF_ID, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

        //add water
        val btnGota = findViewById(R.id.btnGota) as FrameLayout
        btnGota.setOnClickListener {
            addWater(100)
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }

        val btnVaso = findViewById(R.id.btnVaso) as FrameLayout
        btnVaso.setOnClickListener {
            addWater(200)
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }

        val btnBotella = findViewById(R.id.btnBotella) as FrameLayout
        btnBotella.setOnClickListener {
            addWater(500)
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }

        val btnRegadera = findViewById(R.id.btnRegadera) as FrameLayout
        btnRegadera.setOnClickListener {
            addWater(1000)
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }
        launch {
            /** Delay para definir que no es navegacion al crear vista **/
            delay(1000)
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
        Log.d("MUSIC", "ON RESTART GREENHOUSE")
        //Se crea el intent para iniciarlo
        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        var musicPlay = prefs!!.getBoolean("PLAY", false)
        //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
        launch {
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            if (auxUser.music && !musicPlay) {
                Log.d("MUSIC", "STARTING ON RESTART")
                startService(musicService)
            }
            startService(timerService)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addWater(water: Int) {
        launch {
            val actualHistory: History? = mainConnection!!.getHistory(user!!)
            val actualUser: User? = mainConnection!!.getUser(user!!)
            val quantity: Float = (water * 100) / actualUser!!.getMaxWater()
            val cal: Calendar = Calendar.getInstance()
            val day = cal.get(Calendar.DAY_OF_WEEK)
            when (day) {
                Calendar.SUNDAY -> actualHistory!!.sunday = actualHistory.sunday + quantity
                Calendar.MONDAY -> actualHistory!!.monday = actualHistory.monday + quantity
                Calendar.TUESDAY -> actualHistory!!.tuesday = actualHistory.tuesday + quantity
                Calendar.WEDNESDAY -> actualHistory!!.wednesday = actualHistory.wednesday + quantity
                Calendar.THURSDAY -> actualHistory!!.thursday = actualHistory.thursday + quantity
                Calendar.FRIDAY -> actualHistory!!.friday = actualHistory.friday + quantity
                Calendar.SATURDAY -> actualHistory!!.saturday = actualHistory.saturday + quantity
            }
            mainConnection!!.addHistory(user!!, actualHistory!!)
            mainConnection!!.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThan("status", -1)
                .get()
                .addOnSuccessListener { result ->
                    result.documents.forEach {
                        val plant = it.toObject(UserPlants::class.java)
                        mainConnection!!.BDD.collection("Plants")
                            .document(plant!!.plantId)
                            .get()
                            .addOnSuccessListener { detail ->
                                val aux = detail.toObject(Plants::class.java)!!
                                it.reference.update(
                                    "status",
                                    plant.status + aux.getMoney() * 0.3 + quantity
                                )
                            }
                    }
                    Log.d("Connection", "Regando plantas")
                }
        }
    }
}
