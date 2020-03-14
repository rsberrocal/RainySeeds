package com.rainyteam.rainyseeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<View>(R.id.btnLogin) as Button
        val regTxt = findViewById<View>(R.id.regTxt) as TextView

        btnLogin.setOnClickListener(View.OnClickListener {
                view -> login()
        })

        regTxt.setOnClickListener(View.OnClickListener {
                view -> register()
        })
    }

    private fun login() {
        val emailTxt = findViewById<View>(R.id.eT_Email) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_Password) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful){
                    startActivity(Intent(this, xxxxx :: class.java))
                    Toast.makeText(this, "@string/ExitLogin", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }

            })
        } else {
            Toast.makeText(this, "@string/ErrorLogin", Toast.LENGTH_LONG).show()
        }
    }

    private fun register () {
        startActivity(Intent(this, SigninActivity :: class.java))
    }
}
