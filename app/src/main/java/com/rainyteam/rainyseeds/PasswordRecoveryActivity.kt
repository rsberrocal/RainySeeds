package com.rainyteam.rainyseeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class PasswordRecoveryActivity : AppCompatActivity() {

    val btnReturnLogin = findViewById<View>(R.id.btnReturnLogin) as Button
    val btnSendPass = findViewById<View>(R.id.btnSendPassword) as Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_recovery_layout)

        btnReturnLogin.setOnClickListener(View.OnClickListener {
            val returnLogin = Intent(this, LoginActivity::class.java)
            startActivity(returnLogin)
        })

        btnSendPass.setOnClickListener(View.OnClickListener { view ->
            sendPassword()
        })
    }


    fun sendPassword() {

    }
}
