package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.services.MusicService
import kotlinx.android.synthetic.main.user_info_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserInfoActivity() : MusicAppCompatActivity(), CoroutineScope{
    val mAuth = FirebaseAuth.getInstance()

    var mainConnection: Connection? = null

    val PREF_NAME = "USER"
    var userName: String? = ""
    var prefs: SharedPreferences? = null

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.userName = prefs!!.getString("USER_ID", "")
        setUser(userName)

        userBackArrowButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down_to_up, R.anim.slide_stop)
        }

        logoutBtn.setOnClickListener {
            var music = Intent(this, MusicService::class.java)
            stopService(music)
            var edit = prefs!!.edit()
            edit.remove("USER_ID")
            edit.apply()
            val intent = Intent(this, LoginActivity::class.java)
            mAuth.signOut()
            finish()
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)

        }
    }
    fun setUser(user: String?){
        launch {
            var actualUser = mainConnection!!.getUser(user!!)
            textName!!.text = actualUser?.getUsername()
            textAge!!.text =  actualUser?.getAge().toString()
            textWeight!!.text = actualUser?.getWeight().toString()
            textEmail!!.text = actualUser?.getEmail()
            textHeight!!.text = actualUser?.getHeight().toString()
            textSex!!.text = actualUser?.getSex()
            textExercise!!.text = actualUser?.getExercise().toString()
        }
    }

}
