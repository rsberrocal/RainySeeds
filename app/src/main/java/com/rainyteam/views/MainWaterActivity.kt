package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.History
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.coroutines.*
import java.nio.channels.Channel
import kotlin.coroutines.CoroutineContext


class MainWaterActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var userName: String? = null
    var textWaterPercent: TextView? = null

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
        setContentView(R.layout.main_water_layout)

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, "")

        this.mainConnection = Connection()
        async { getWater(userName) }
        setUser(userName)

        this.textWaterPercent = findViewById(R.id.waterPercent)


        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up_to_down, R.anim.slide_stop)
        }
        waterButton.setOnClickListener {
            val intent = Intent(this, IntroduceWaterActivity::class.java)
            startActivity(intent)
        }

        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
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

    fun setUser(user: String?) {
        launch {
            var actualUser = mainConnection!!.getUser(user!!)
            userNameText!!.text = actualUser?.getUsername()
            userAge!!.text = "Age: " + actualUser?.getAge().toString()
            userWeight!!.text = "Weight: " + actualUser?.getWeight().toString()

        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWater(user: String?) {

        launch {
            var actualHistory: History? = mainConnection!!.getHistory(user!!)
            var cal: Calendar = Calendar.getInstance()
            var day = cal.get(Calendar.DAY_OF_WEEK)
            var actualUserWater: Float = 30f
            when (day) {
                Calendar.SUNDAY -> actualUserWater = actualHistory!!.sunday
                Calendar.MONDAY -> actualUserWater = actualHistory!!.monday
                Calendar.TUESDAY -> actualUserWater = actualHistory!!.tuesday
                Calendar.WEDNESDAY -> actualUserWater = actualHistory!!.wednesday
                Calendar.THURSDAY -> actualUserWater = actualHistory!!.thursday
                Calendar.FRIDAY -> actualUserWater = actualHistory!!.friday
                Calendar.SATURDAY -> actualUserWater = actualHistory!!.saturday
            }
            setWaterImage(actualUserWater)
        }

    }

    fun setWaterImage(actualUserWater: Float) {
        when {
            actualUserWater <= 10 && actualUserWater > 5 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_5)
            )
            actualUserWater <= 20 && actualUserWater > 10 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_10)
            )
            actualUserWater <= 30 && actualUserWater > 20 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_20)
            )
            actualUserWater <= 40 && actualUserWater > 30 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_30)
            )
            actualUserWater <= 50 && actualUserWater > 40 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_40)
            )
            actualUserWater <= 60 && actualUserWater > 50 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_50)
            )
            actualUserWater <= 70 && actualUserWater > 60 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_60)
            )
            actualUserWater <= 80 && actualUserWater > 70 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_70)
            )
            actualUserWater <= 90 && actualUserWater > 80 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_80)
            )
            actualUserWater <= 95 && actualUserWater > 90 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_90)
            )
        }
        textWaterPercent!!.text = actualUserWater.toString() + "%"

    }


}
