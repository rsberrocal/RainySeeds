package com.rainyteam.views

import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rainyteam.controller.R
import com.rainyteam.model.*
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.introduce_water_layout.*
import kotlinx.coroutines.*
import java.time.Instant
import java.util.*
import kotlin.coroutines.CoroutineContext

class IntroduceWaterActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var mainConnection: Connection? = null
    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null
    val MAX_WATER = 999
    var user: String? = ""

    var firstNav = false

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        setContentView(R.layout.introduce_water_layout)
        falseBackButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(
                R.anim.slide_stop,
                R.anim.slide_stop
            )
        }

        this.mainConnection = Connection()
        prefs = getSharedPreferences(PREF_ID, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

        //add water
        val btnGota = findViewById(R.id.btnGota) as FrameLayout
        btnGota.setOnClickListener {
            addWater(100)
        }

        val btnVaso = findViewById(R.id.btnVaso) as FrameLayout
        btnVaso.setOnClickListener {
            addWater(200)
        }

        val btnBotella = findViewById(R.id.btnBotella) as FrameLayout
        btnBotella.setOnClickListener {
            addWater(500)
        }

        val btnRegadera = findViewById(R.id.btnRegadera) as FrameLayout
        btnRegadera.setOnClickListener {
            addWater(1000)
        }

        val btnManual = findViewById(R.id.manualWaterBtn) as TextView
        btnManual.setOnClickListener {
            val text = findViewById(R.id.manualWaterTxt) as EditText
            if (!text.text.isBlank()) {
                if (text.text.toString().toLong() < 99999999) {
                    addWater(text.text.toString().toInt())
                } else {
                    Toast.makeText(this, "You can not drink too much :(", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Need water to continue", Toast.LENGTH_LONG).show()
            }
        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val returnIntroduceWaterActivity = Intent(this, MainWaterActivity::class.java)
        startActivity(returnIntroduceWaterActivity)
        finish()
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addWater(water: Int) {
        launch {
            val actualHistory: History? = mainConnection!!.getHistory(user!!)
            val actualUser: User? = mainConnection!!.getUser(user!!)
            val quantity: Float = (water * 100) / actualUser!!.getMaxWater()
            val cal: Calendar = Calendar.getInstance()
            val day = cal.get(Calendar.DAY_OF_WEEK)
            var hasOverload = false
            when (day) {
                Calendar.SUNDAY -> {
                    actualHistory!!.sunday = actualHistory.sunday + quantity
                    if (actualHistory.sunday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.MONDAY -> {
                    actualHistory!!.monday = actualHistory.monday + quantity
                    if (actualHistory.monday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.TUESDAY -> {
                    actualHistory!!.tuesday = actualHistory.tuesday + quantity
                    if (actualHistory.tuesday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.WEDNESDAY -> {
                    actualHistory!!.wednesday = actualHistory.wednesday + quantity
                    if (actualHistory.wednesday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.THURSDAY -> {
                    actualHistory!!.thursday = actualHistory.thursday + quantity
                    if (actualHistory.thursday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.FRIDAY -> {
                    actualHistory!!.friday = actualHistory.friday + quantity
                    if (actualHistory.friday > MAX_WATER) {
                        hasOverload = true
                    }
                }
                Calendar.SATURDAY -> {
                    actualHistory!!.saturday = actualHistory.saturday + quantity
                    if (actualHistory.saturday > MAX_WATER) {
                        hasOverload = true
                    }
                }
            }

            //El usuario se ha pasado bebiendo
            if (hasOverload) {
                mainConnection!!.BDD.collection("User-Plants")
                    .whereEqualTo("userId", user)
                    .whereGreaterThan("status", -1)
                    .get()
                    .addOnSuccessListener { result ->
                        val items = result.documents.size
                        var count = 0
                        result.documents.forEach {
                            it.reference.update("status", -1)
                            count++
                            if (count == items) {
                                Toast.makeText(this@IntroduceWaterActivity, "You've drowned all your plants by drinking too much :(", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
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
                                    var statusToadd = 0
                                    if (getStatusHistory(actualHistory) >= 100) {
                                        statusToadd = (plant.status - aux.getMoney() * 0.2 + quantity).toInt()
                                        Toast.makeText(applicationContext, "You're drowning your plants!!", Toast.LENGTH_LONG).show()
                                    } else {
                                        statusToadd = (plant.status + aux.getMoney() * 0.3 + quantity).toInt()
                                    }
                                    it.reference.update("status", statusToadd)
                                }
                        }
                        Log.d("Connection", "Regando plantas")
                    }
            }

            val intent = Intent(applicationContext, MainWaterActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(
                R.anim.slide_down_to_up,
                R.anim.slide_stop
            )
            prefs!!.edit().putLong(getDay(), Instant.now().epochSecond).apply()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getStatusHistory(history: History): Float {
        val cal: Calendar = Calendar.getInstance()
        val day = cal.get(Calendar.DAY_OF_WEEK)
        when (day) {
            Calendar.SUNDAY -> return history.sunday
            Calendar.MONDAY -> return history.monday
            Calendar.TUESDAY -> return history.tuesday
            Calendar.WEDNESDAY -> return history.wednesday
            Calendar.THURSDAY -> return history.thursday
            Calendar.FRIDAY -> return history.friday
            Calendar.SATURDAY -> return history.saturday
        }
        return 0f
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded main water")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav) {
            this.firstNav = true
        } else {
            Log.e("MUSIC", "************* foregrounded main water")
            // App in foreground
            Log.d("MUSIC", "ON RESTART MainWaterActivity")
            //Se crea el intent para iniciarlo
            val musicService = Intent(this, MusicService::class.java)
            val timerService = Intent(this, TimerService::class.java)
            //var musicPlay = prefs!!.getBoolean("PLAY", false)
            //val isNav = prefs!!.getBoolean("NAV", false);
            //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
            launch {
                var auxUser: User = mainConnection!!.getUser(user!!)!!
                if (auxUser.music) {
                    Log.d("MUSIC", "STARTING ON RESTART")
                    startService(musicService)
                }
                startService(timerService)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDay(): String {
        var cal: Calendar = Calendar.getInstance()
        var day = cal.get(Calendar.DAY_OF_WEEK)
        when (day) {
            Calendar.SUNDAY -> return "sunday"
            Calendar.MONDAY -> return "monday"
            Calendar.TUESDAY -> return "tuesday"
            Calendar.WEDNESDAY -> return "wednesday"
            Calendar.THURSDAY -> return "thursday"
            Calendar.FRIDAY -> return "friday"
            Calendar.SATURDAY -> return "saturday"
        }
        return ""
    }

}
