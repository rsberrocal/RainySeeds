package com.rainyteam.controller

import android.content.DialogInterface
import android.content.Intent
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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        val btnLogin = findViewById<View>(R.id.btnLogin) as Button
        val signup = findViewById<View>(R.id.tV_Signup) as TextView
        val forgotPass = findViewById<View>(R.id.tV_PasswordRecovery) as TextView
        val btnLoginGoogle = findViewById<View>(R.id.btnGoogle) as Button
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this,mGoogleSignInOptions)
        btnLogin.setOnClickListener(View.OnClickListener {
                view -> login()
        })

        signup.setOnClickListener(View.OnClickListener {
                val signin = Intent(this, SigninActivity::class.java)
                startActivity(signin)
        })

        forgotPass.setOnClickListener(View.OnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.ForgotPass)
            val view = layoutInflater.inflate(R.layout.dialog_forgotpass, null)
            val email = view.findViewById<EditText>(R.id.eT_EmailFP)
            builder.setView(view)
            builder.setPositiveButton(R.string.Reset, DialogInterface.OnClickListener { _,  _ ->
                forgot(email)
            })
            builder.setNegativeButton(R.string.Close, DialogInterface.OnClickListener { _, _ ->  })
            builder.show()
        })

        btnLoginGoogle.setOnClickListener(View.OnClickListener {
                view -> loginGoogle()
        })
    }

    private fun forgot(email : EditText){
        var emailT = email.text.toString()

        mAuth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener{ task ->
            if (task.isSuccessful) {
                Toast.makeText(this, R.string.EmailSent, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login() {
        val emailTxt = findViewById<View>(R.id.eT_Email) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_Password) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()
        if (!email.isEmpty() && !password.isEmpty()) {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener { task ->
                if (task.isSuccessful){
                    Toast.makeText(this, R.string.ExitLogin, Toast.LENGTH_LONG).show()
                    val principal = Intent(this, GreenhouseActivity::class.java)
                    startActivity(principal)
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }

            })
        } else {
            Toast.makeText(this, R.string.ErrorLogin, Toast.LENGTH_LONG).show()
        }
    }

    private fun loginGoogle() {
        val signinIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account  =  task.getResult(ApiException::class.java)!!

                authWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed:(", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun authWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, R.string.ExitLogin, Toast.LENGTH_LONG).show()
                val principal = Intent(this, GreenhouseActivity::class.java)
                startActivity(principal)
                finish()
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
            }
        }
    }

}
