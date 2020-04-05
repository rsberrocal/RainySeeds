package com.rainyteam.views

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rainyteam.controller.R
import com.rainyteam.model.Connection

class LoginActivity : AppCompatActivity() {

    var mAuth: FirebaseAuth? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    var mainConnection: Connection? = null

    //shared preferences
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        //Start the controllers
        //this.mainController = MainController() //Pass this main controller over the views
        //mAuth = mainController!!.getInstanceFirebaseAuth()

        mAuth = mainConnection!!.mAuth()

        prefs = getSharedPreferences(PREF_NAME, 0)
        val hasUser = prefs!!.getString("USER_ID", null)
        if (hasUser != null) {
            val principal = Intent(this, GreenhouseActivity::class.java)
            finish()
            startActivity(principal)
        }

        val btnLogin = findViewById<View>(R.id.btnLogin) as Button
        val signup = findViewById<View>(R.id.tV_Signup) as TextView
        val forgotPass = findViewById<View>(R.id.tV_PasswordRecovery) as TextView
        val btnLoginGoogle = findViewById<View>(R.id.btnGoogle) as SignInButton


        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)


        btnLogin.setOnClickListener(View.OnClickListener {
            login()
        })

        signup.setOnClickListener(View.OnClickListener {
            val signin = Intent(this, SigninActivity::class.java)
            startActivity(signin)
        })

        forgotPass.setOnClickListener(View.OnClickListener {
            val title = AlertDialog.Builder(this)
            title.setTitle(R.string.ForgotPass)
            val view = layoutInflater.inflate(R.layout.dialog_forgotpass, null)
            val email = view.findViewById<EditText>(R.id.eT_EmailFP)
            title.setView(view)
            title.setPositiveButton(R.string.Reset, DialogInterface.OnClickListener { _, _ ->
                forgot(email)
            })
            title.setNegativeButton(R.string.Close, DialogInterface.OnClickListener { _, _ -> })
            title.show()
        })

        btnLoginGoogle.setOnClickListener(View.OnClickListener {
            loginGoogle()
        })
    }

    private fun forgot(email: EditText) {

        mAuth!!.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this,
                    R.string.EmailSent, Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUser(id: String) {

        val editor = prefs!!.edit()
        editor.putString("USER_ID", id)
        editor.apply()
    }

    private fun login() {
        val emailTxt = findViewById<View>(R.id.eT_Email) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_Password) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            R.string.ExitLogin, Toast.LENGTH_LONG
                        ).show()

                        setUser(email)

                        val principal = Intent(this, GreenhouseActivity::class.java)
                        startActivity(principal)
                    } else {
                        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                    }

                })
        } else {
            Toast.makeText(
                this,
                R.string.ErrorLogin, Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun loginGoogle() {
        val signinIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                authWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(
                    this,
                    R.string.GoogleSigninError, Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth!!.signInWithCredential(credential).addOnCompleteListener {
            val isNewUser: Boolean = it.getResult()!!.additionalUserInfo!!.isNewUser()
            if (it.isSuccessful) {
                setUser(acct.email!!)
                if (isNewUser) {
                    val principal = Intent(this, UserInfoActivity::class.java)
                    startActivity(principal)
                    finish()
                } else {
                    val principal = Intent(this, GreenhouseActivity::class.java)
                    startActivity(principal)
                    finish()
                }
                Toast.makeText(
                    this,
                    R.string.ExitLogin, Toast.LENGTH_LONG
                ).show()

            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }
}
