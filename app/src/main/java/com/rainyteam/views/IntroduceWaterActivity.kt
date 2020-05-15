package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.History
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
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
            addWater(50)
        }

        val btnVaso = findViewById(R.id.btnVaso) as FrameLayout
        btnGota.setOnClickListener {
            addWater(100)
        }

        val btnBotella = findViewById(R.id.btnBotella) as FrameLayout
        btnGota.setOnClickListener {
            addWater(200)
        }

        val btnRegadera = findViewById(R.id.btnRegadera) as FrameLayout
        btnGota.setOnClickListener {
            addWater(300)
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
            var auxUser: User = mainConnection!!.getUser(user!!)!!
            if (auxUser.music && !musicPlay) {
                startService(musicService)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun addWater(water: Int) {
        launch {
            var actualHistory: History? = mainConnection!!.getHistory(user!!)
            var actualUser: User? = mainConnection!!.getUser(user!!)
            var quantity: Float = (water * 100) / actualUser!!.getMaxWater()
            var cal: Calendar = Calendar.getInstance()
            var day = cal.get(Calendar.DAY_OF_WEEK)
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
            //Pillar el maximo del usuario
            //calcular su porcentaje y añadirlo al history
            //hacer reload de la botella
        }
    }
}
