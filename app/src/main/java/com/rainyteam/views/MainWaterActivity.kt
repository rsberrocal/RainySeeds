package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_water_layout)

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.userName = prefs!!.getString("USER_ID", "")

        this.mainConnection = Connection()

        setUser(userName)
        this.textWaterPercent = findViewById(R.id.waterPercent)
        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
        }

        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
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
}
