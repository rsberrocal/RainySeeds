package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import kotlinx.android.synthetic.main.signin_layout.*

class SigninActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    var DataInst: FirebaseDatabase? = null
    var mDatabase: DatabaseReference? = null
    var mainConnection: Connection? = null
    var mBDD: FirebaseFirestore? = null
    val PREF_NAME = "USER"
    val PREF_ID = "USER_ID"
    var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signin_layout)
        prefs = getSharedPreferences(PREF_NAME, 0)
        this.mainConnection = Connection()
        DataInst = mainConnection!!.mDatabase()
        mDatabase = DataInst!!.getReference("Emails")
        mAuth = mainConnection!!.mAuth()
        mBDD = mainConnection!!.mBDD()

        val btnSignin = findViewById<View>(R.id.btnSignin) as Button
        val btnReturnLogin = findViewById<View>(R.id.btnReturnLogin) as Button

        btnSignin.setOnClickListener { view ->
            btnSignin.isClickable=false
            register()
        }

        btnReturnLogin.setOnClickListener {
            val returnLogin = Intent(this, LoginActivity::class.java)
            startActivity(returnLogin)
            finish()
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val returnLogin = Intent(this, LoginActivity::class.java)
        startActivity(returnLogin)
        finish()
        overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)
    }

    private fun register() {
        val emailTxt = findViewById<View>(R.id.eT_EmailSignin) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_PasswordSignin) as EditText
        val confirmPasswordTxt = findViewById<View>(R.id.eT_ConfirmPassword) as EditText
        val dataUsers = hashMapOf(
            "username" to "",
            "age" to 0,
            "weight" to 0,
            "height" to 0,
            "sex" to "",
            "exercise" to 0,
            "maxWater" to 0.0f,
            "rainycoins" to 0,
            "hasInfo" to false,
            "music" to true
        )
        val dataHistory = hashMapOf(
            "monday" to 0.0f,
            "tuesday" to 0.0f,
            "wednesday" to 0.0f,
            "thursday" to 0.0f,
            "friday" to 0.0f,
            "saturday" to 0.0f,
            "sunday" to 0.0f
        )
        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        var confirmPassword = confirmPasswordTxt.text.toString()
        val dataPlant = hashMapOf(
            "status" to 100,
            "userId" to email,
            "plantId" to "Cactus"
        )

        if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if (password == confirmPassword) {
                if (password.length >= 6) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        mAuth!!.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, OnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = mAuth!!.currentUser
                                    val uid = user!!.uid
                                    mDatabase!!.child(uid).child("Email").setValue(email)
                                    Toast.makeText(
                                        this,
                                        R.string.ExitSignin, Toast.LENGTH_LONG
                                    ).show()
                                    mBDD!!.collection("Users").document(email).set(dataUsers)
                                    mBDD!!.collection("User-Plants").document("$email-Cactus")
                                        .set(dataPlant)
                                    mBDD!!.collection("History").document(email).set(dataHistory)
                                    setUser(email)
                                    val btnSignin = Intent(this, SignIn2Activity::class.java)
                                    startActivity(btnSignin)
                                    overridePendingTransition(
                                        R.anim.slide_down_to_up,
                                        R.anim.slide_stop
                                    )
                                    finish()
                                } else {
                                    Log.d("Connection", task.exception?.message!!)
                                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                                    btnSignin.isClickable=true
                                }
                            })
                    } else {
                        Toast.makeText(
                            this,
                            R.string.WrongEmail, Toast.LENGTH_LONG
                        ).show()
                        btnSignin.isClickable=true
                    }
                } else {
                    Toast.makeText(
                        this,
                        R.string.ErrorPasswordLength, Toast.LENGTH_LONG
                    ).show()
                    btnSignin.isClickable=true
                }
            } else {
                Toast.makeText(
                    this,
                    R.string.ErrorPassword, Toast.LENGTH_LONG
                ).show()
                btnSignin.isClickable=true
            }
        } else {
            Toast.makeText(
                this,
                R.string.ErrorLogin, Toast.LENGTH_LONG
            ).show()
            btnSignin.isClickable=true
        }
    }

    private fun setUser(id: String) {
        prefs!!.edit().putString(PREF_ID, id).apply()
    }
}

