package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.android.synthetic.main.sign_in_2_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SignIn2Activity : AppCompatActivity(), CoroutineScope {

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
        setContentView(R.layout.sign_in_2_layout)

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")

        btnSignin.setOnClickListener {
            launch {
                var actualUser: User? = mainConnection!!.getUser(user!!)
                actualUser!!.setName(inputUsername.text.toString())
                actualUser!!.setWeight(inputWeight.text.toString().toInt())
                actualUser!!.setHeight(inputHeight.text.toString().toFloat())
                actualUser!!.setExercise(inputHoursExercise.text.toString().toInt())
                actualUser!!.setSex(sexOption.checkedRadioButtonId.toString())
                //hasInfo = true
            }
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
        }
    }
}
