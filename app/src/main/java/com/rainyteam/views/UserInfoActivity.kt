package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.rainyteam.controller.R
import com.rainyteam.services.MusicService
import kotlinx.android.synthetic.main.user_info_layout.*

class UserInfoActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()

    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)

        prefs = getSharedPreferences(PREF_NAME, 0)

        userBackArrowButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
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

        }
    }

}
