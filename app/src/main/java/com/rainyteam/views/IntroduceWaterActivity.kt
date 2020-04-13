package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.History
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.introduce_water_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class IntroduceWaterActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null
    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = ""

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

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
        val btnGota = findViewById(R.id.btnGota) as Button
        btnGota.setOnClickListener {
            addWater(50)
        }

        val btnVaso = findViewById(R.id.btnVaso) as Button
        btnGota.setOnClickListener {
            addWater(100)
        }

        val btnBotella = findViewById(R.id.btnBotella) as Button
        btnGota.setOnClickListener {
            addWater(200)
        }

        val btnRegadera = findViewById(R.id.btnRegadera) as Button
        btnGota.setOnClickListener {
            addWater(300)
        }
    }

    fun addWater(water: Int) {
        launch {
            var actualHistory: History? = mainConnection!!.getHistory(user!!)
            var actualUser: User? = mainConnection!!.getUser(user)
            
            //Pillar el maximo del usuario
            //calcular su porcentaje y añadirlo al history
            //hacer reload de la botella
            println(water);
        }
    }
}
