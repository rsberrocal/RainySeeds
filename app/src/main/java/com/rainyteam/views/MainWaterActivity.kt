package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.History
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class MainWaterActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    val PREF_NAME = "USER"
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

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.userName = prefs!!.getString("USER_ID", "")

        this.mainConnection = Connection()
        setWaterImage(userName)
        setUser(userName)
        this.textWaterPercent = findViewById(R.id.waterPercent)
        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
        }

        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_up, R.anim.slide_stop)
        }
        waterButton.setOnClickListener {
            val intent = Intent(this, IntroduceWaterActivity::class.java)
            startActivity(intent)
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
    fun setWaterImage(user: String?) {

        var actualUserWater: Float = 0.0f
        launch {
            var actualHistory: History? = mainConnection!!.getHistory(user!!)
            var cal: Calendar = Calendar.getInstance()
            var day = cal.get(Calendar.DAY_OF_WEEK)
            when (day) {
                Calendar.SUNDAY -> actualUserWater = actualHistory!!.sunday
                Calendar.MONDAY -> actualUserWater = actualHistory!!.monday
                Calendar.TUESDAY -> actualUserWater = actualHistory!!.tuesday
                Calendar.WEDNESDAY -> actualUserWater = actualHistory!!.wednesday
                Calendar.THURSDAY -> actualUserWater = actualHistory!!.thursday
                Calendar.FRIDAY -> actualUserWater = actualHistory!!.friday
                Calendar.SATURDAY -> actualUserWater = actualHistory!!.saturday
            }
        }
    }
}
