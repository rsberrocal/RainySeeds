package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import com.androdocs.weatherapp.TrueWeatherActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class ChargingScreen : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    var mBDD: FirebaseFirestore? = null
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("MAIN", "Finish app");
        //exitProcess(0)
        moveTaskToBack(true)
        exitProcess(-1)
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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_charging_screen)

        this.mainConnection = Connection()
        mBDD = mainConnection!!.mBDD()

        prefs = getSharedPreferences(PREF_NAME, 0)
        //Setting default play
        prefs!!.edit().putBoolean("PLAY", false).apply()
        val hasUser = prefs!!.getString("USER_ID", null)
        if (hasUser != null) {
            launch {
                val userData = mainConnection!!.getUser(hasUser)
                if (userData!!.hasInfo) {
                    var lastTime = prefs!!.getLong(getDay(), 0)
                    if (lastTime != 0L) {
                        val now = Date()
                        val l1 = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(lastTime),
                            ZoneId.systemDefault()
                        )
                        val l2 = LocalDateTime.ofInstant(now.toInstant(), ZoneId.systemDefault())
                        val num = ChronoUnit.HALF_DAYS.between(l1, l2)
                        if (num > 0) {
                            val principal =
                                Intent(this@ChargingScreen, TrueWeatherActivity::class.java)
                            startActivity(principal)
                            finish()
                        } else {
                            val principal =
                                Intent(this@ChargingScreen, GreenhouseActivity::class.java)
                            startActivity(principal)
                            finish()
                        }
                    } else {
                        val principal = Intent(this@ChargingScreen, GreenhouseActivity::class.java)
                        startActivity(principal)
                        finish()
                    }
                }
            }
        } else {
            val principal = Intent(this, LoginActivity::class.java)
            startActivity(principal)
            finish()
        }

    }
}
