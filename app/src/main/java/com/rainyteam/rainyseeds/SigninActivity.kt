package com.rainyteam.rainyseeds

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SigninActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var  mDatabase : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)

        val btnSignin = findViewById<View>(R.id.btnSignin) as Button
        val btnReturnLogin = findViewById<View>(R.id.btnReturnLogin) as Button

        mDatabase = FirebaseDatabase.getInstance().getReference("Emails")

        btnSignin.setOnClickListener(View.OnClickListener {
            view -> register ()
        })

        btnReturnLogin.setOnClickListener(View.OnClickListener {
            val returnLogin = Intent(this, LoginActivity::class.java)
            startActivity(returnLogin)
        })
    }

    private fun register () {
        val emailTxt = findViewById<View>(R.id.eT_EmailSignin) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_Password) as EditText
        val confirmPasswordTxt = findViewById<View>(R.id.eT_ConfirmPassword) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var confirmPassword = confirmPasswordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (password == confirmPassword) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful){
                        val user = mAuth.currentUser
                        val uid = user!!.uid
                        mDatabase.child(uid).child("Email").setValue(email)
                        Toast.makeText(this, R.string.ExitSignin, Toast.LENGTH_LONG).show()
                        val btnSignin = Intent(this, GreenhouseActivity::class.java)
                        startActivity(btnSignin)
                    } else {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    }
                })
            } else {
                Toast.makeText(this, R.string.ErrorPassword, Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, R.string.ErrorLogin, Toast.LENGTH_LONG).show()
        }

            }

    }

