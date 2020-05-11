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
import kotlinx.android.synthetic.main.introduce_water_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.CoroutineContext

class IntroduceWaterActivity : MusicAppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    //shared

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
        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

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
            mainConnection!!.addHistory(user!!,actualHistory!!)
            //Pillar el maximo del usuario
            //calcular su porcentaje y añadirlo al history
            //hacer reload de la botella
        }
    }
}
