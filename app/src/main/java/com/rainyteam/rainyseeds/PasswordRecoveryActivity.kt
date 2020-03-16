package com.rainyteam.rainyseeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class PasswordRecoveryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.password_recovery_layout)

        val btnReturnLogin = findViewById<View>(R.id.btnReturnLogin) as Button
        val btnSendPass = findViewById<View>(R.id.btnSendPassword) as Button

        btnReturnLogin.setOnClickListener(View.OnClickListener {
            /*val returnLogin = Intent(this, LoginActivity::class.java)
            startActivity(returnLogin)*/
            /** En vez de crear un nuevo actvity, este se cierra **/
            finish();
        })

        btnSendPass.setOnClickListener(View.OnClickListener { view ->
            sendPassword()
        })
    }


    fun sendPassword() {

    }
}
