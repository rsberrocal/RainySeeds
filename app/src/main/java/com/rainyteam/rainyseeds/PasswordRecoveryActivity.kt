package com.rainyteam.rainyseeds

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class PasswordRecoveryActivity : AppCompatActivity() {

    val btnReturnLogin = findViewById<View>(R.id.btnReturnLogin) as Button
    val btnSendPass = findViewById<View>(R.id.btnSendPassword) as Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_recovery)

        btnReturnLogin.setOnClickListener(View.OnClickListener {
                view -> returntoLogin ()
        })

        btnSendPass.setOnClickListener(View.OnClickListener {
                view -> sendPassword ()
        })
    }

    private fun returntoLogin () {

    }

    private fun sendPassword () {

    }
}
