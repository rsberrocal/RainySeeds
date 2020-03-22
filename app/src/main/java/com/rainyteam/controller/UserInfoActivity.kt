package com.rainyteam.controller

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.introduce_water_layout.*
import kotlinx.android.synthetic.main.user_info_layout.*

class UserInfoActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)
        userBackArrowButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }
        logoutBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            mAuth.signOut()
            startActivity(intent)
        }
    }

}
