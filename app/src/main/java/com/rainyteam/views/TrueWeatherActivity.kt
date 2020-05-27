package com.androdocs.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import com.rainyteam.views.GreenhouseActivity
import com.rainyteam.views.LoginActivity
import kotlinx.android.synthetic.main.activity_true_weather.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext

class TrueWeatherActivity : AppCompatActivity(), CoroutineScope {
    var mainConnection: Connection? = null
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = null
    var temp: Float = 0.0f
    val CITY: String = "Barcelona"
    val API: String = "8118ed6ee68db2debfaaa5a44c832918"
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_true_weather)
        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")
        weatherTask().execute()
        reCalculateUserMaxWater()
        falseGreenHouseLayout.setOnClickListener{
            setContentView(R.layout.greenhouse_layout)
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun reCalculateUserMaxWater() {
        launch {
            var actualUser: User = mainConnection!!.getUser(user!!)!!
            actualUser.setMaxWater(temp)
            mainConnection!!.setUser(actualUser)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val returnTrueWeatherActivity = Intent(this, GreenhouseActivity::class.java)
        startActivity(returnTrueWeatherActivity)
        finish()
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */

        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                temp = main.getString("temp").toFloat()
                var message:String =""
                if(temp<14){
                    message="Today is a cold day, you shouldn't worry about drinking more water as usual."
                }
                else if(temp>=14 && temp<27){
                    message="Today it's a common day, not cold, not hot. Just drink the same water as usual."
                }
                else{
                    message="Today it's a hot day, you should drink more water than usual. "
                }
                weatherText.text = message

            }
            catch(e: java.lang.Exception){
            }
        }
    }
}