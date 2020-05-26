package com.rainyteam.views

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.androdocs.weatherapp.TrueWeatherActivity
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
import com.google.firebase.firestore.FirebaseFirestore
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.login_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.coroutines.CoroutineContext
import kotlin.system.exitProcess

class LoginActivity : AppCompatActivity(), CoroutineScope {

    var mAuth: FirebaseAuth? = null
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    val RC_SIGN_IN: Int = 1
    var mainConnection: Connection? = null
    var mBDD: FirebaseFirestore? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    //shared preferences
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_layout)

        this.mainConnection = Connection()
        mAuth = mainConnection!!.mAuth()
        mBDD = mainConnection!!.mBDD()

        prefs = getSharedPreferences(PREF_NAME, 0)
        //Setting default play
        prefs!!.edit().putBoolean("PLAY", false).apply()

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
            btnLogin.isClickable=false
            login()
        })

        signup.setOnClickListener(View.OnClickListener {
            val signin = Intent(this, SigninActivity::class.java)
            startActivity(signin)
            overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_stop)
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

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d("MAIN", "Finish app");
        //exitProcess(0)
        moveTaskToBack(true)
        exitProcess(-1)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun login() {
        val emailTxt = findViewById<View>(R.id.eT_Email) as EditText
        val passwordTxt = findViewById<View>(R.id.eT_Password) as EditText

        var email = emailTxt.text.toString()
        var password = passwordTxt.text.toString()

        if (!email.isEmpty() && !password.isEmpty()) {
            try {
                mAuth!!.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                R.string.ExitLogin, Toast.LENGTH_LONG
                            ).show()
                            setUser(email)
                            launch {
                                var auxUser: User = mainConnection!!.getUser(email)!!
                                //Si no tiene la informacion
                                if (!auxUser.hasInfo) {
                                    val principal =
                                        Intent(applicationContext, SignIn2Activity::class.java)
                                    startActivity(principal)
                                    overridePendingTransition(
                                        R.anim.slide_down_to_up,
                                        R.anim.slide_stop
                                    )
                                    finish()
                                } else {
                                    var lastTime = prefs!!.getLong(getDay(), 0)
                                    if (lastTime != 0L) {
                                        val now = Date()
                                        val l1 = LocalDateTime.ofInstant(
                                            Instant.ofEpochMilli(lastTime),
                                            ZoneId.systemDefault()
                                        )
                                        val l2 = LocalDateTime.ofInstant(
                                            now.toInstant(),
                                            ZoneId.systemDefault()
                                        )
                                        val num = ChronoUnit.HALF_DAYS.between(l1, l2)
                                        if (num > 0) {
                                            val principal = Intent(
                                                applicationContext,
                                                TrueWeatherActivity::class.java
                                            )
                                            startActivity(principal)
                                        } else {
                                            val principal =
                                                Intent(
                                                    applicationContext,
                                                    GreenhouseActivity::class.java
                                                )
                                            startActivity(principal)
                                        }
                                    } else {
                                        val principal =
                                            Intent(
                                                applicationContext,
                                                GreenhouseActivity::class.java
                                            )
                                        startActivity(principal)
                                    }
                                    finish()
                                    overridePendingTransition(
                                        R.anim.slide_up_to_down,
                                        R.anim.slide_stop
                                    )
                                }
                            }
                        } else {
                            Toast.makeText(
                                this,
                                R.string.LoginError, Toast.LENGTH_LONG
                            ).show()
                            btnLogin.isClickable=true
                        }
                    })
            } catch (e: Exception) {
                Log.d("Connection: ", e.message)
                Toast.makeText(
                    this,
                    "Error", Toast.LENGTH_LONG
                ).show()
                btnLogin.isClickable=true
            }
        } else {
            Toast.makeText(
                this,
                R.string.ErrorLogin, Toast.LENGTH_LONG
            ).show()
            btnLogin.isClickable=true
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getDay(): String {
        var cal: Calendar = Calendar.getInstance()
        var day = cal.get(Calendar.DAY_OF_WEEK)
        when (day) {
            Calendar.SUNDAY -> return "sunday"
            Calendar.MONDAY -> return "monday"
            Calendar.TUESDAY -> return "tuesday"
            Calendar.WEDNESDAY -> return "wednesday"
            Calendar.THURSDAY -> return "thursday"
            Calendar.FRIDAY -> return "friday"
            Calendar.SATURDAY -> return "saturday"
        }
        return ""
    }

    private fun loginGoogle() {
        val signinIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signinIntent, RC_SIGN_IN)
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
                Log.d("Firebase", e.message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun authWithGoogle(acct: GoogleSignInAccount) {
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
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        try {
            mAuth!!.signInWithCredential(credential).addOnCompleteListener {
                val isNewUser: Boolean = it.result!!.additionalUserInfo!!.isNewUser
                if (it.isSuccessful) {
                    val email = acct.email!!
                    val dataPlant = hashMapOf(
                        "status" to 100,
                        "userId" to email,
                        "plantId" to "Cactus"
                    )
                    setUser(acct.email!!)
                    if (isNewUser) {
                        mBDD!!.collection("Users").document(email).set(dataUsers)
                        mBDD!!.collection("User-Plants").document("$email-Cactus").set(dataPlant)
                        mBDD!!.collection("History").document(email).set(dataHistory)
                        val principal = Intent(this, SignIn2Activity::class.java)
                        startActivity(principal)
                        overridePendingTransition(
                            R.anim.slide_down_to_up,
                            R.anim.slide_stop
                        )
                        finish()
                    } else {
                        launch {
                            var auxUser: User = mainConnection!!.getUser(email)!!
                            //Si no tiene la informacion
                            if (!auxUser.hasInfo) {
                                val principal =
                                    Intent(applicationContext, SignIn2Activity::class.java)
                                startActivity(principal)
                                overridePendingTransition(
                                    R.anim.slide_down_to_up,
                                    R.anim.slide_stop
                                )
                                finish()
                            } else {
                                var lastTime = prefs!!.getLong(getDay(), 0)
                                if (lastTime != 0L) {
                                    val now = Date()
                                    val l1 = LocalDateTime.ofInstant(
                                        Instant.ofEpochMilli(lastTime),
                                        ZoneId.systemDefault()
                                    )
                                    val l2 = LocalDateTime.ofInstant(
                                        now.toInstant(),
                                        ZoneId.systemDefault()
                                    )
                                    val num = ChronoUnit.HALF_DAYS.between(l1, l2)
                                    if (num > 0) {
                                        val principal =
                                            Intent(
                                                applicationContext,
                                                TrueWeatherActivity::class.java
                                            )
                                        startActivity(principal)
                                    } else {
                                        val principal =
                                            Intent(
                                                applicationContext,
                                                GreenhouseActivity::class.java
                                            )
                                        startActivity(principal)
                                    }
                                } else {
                                    val principal =
                                        Intent(applicationContext, GreenhouseActivity::class.java)
                                    startActivity(principal)
                                }
                                overridePendingTransition(
                                    R.anim.slide_up_to_down,
                                    R.anim.slide_stop
                                )
                                finish()
                            }
                        }
                    }
                    Toast.makeText(
                        this,
                        R.string.ExitLogin, Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            Log.d("Connection: ", e.message)
            Toast.makeText(
                this,
                "Error", Toast.LENGTH_LONG
            ).show()
        }
    }
}
