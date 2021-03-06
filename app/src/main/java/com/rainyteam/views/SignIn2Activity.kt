package com.rainyteam.views

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.androdocs.weatherapp.TrueWeatherActivity
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.sign_in_2_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.CoroutineContext


class SignIn2Activity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = null
    var format = SimpleDateFormat("dd MMM, YYY", Locale.US)
    var age: Long = 50
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_2_layout)

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")
        inputBirthDate.setOnClickListener {
            showDatePickerDialog();
        }
        btnSignin.setOnClickListener {
            btnSignin.isClickable = false
            if (!inputUsername.text.toString().isEmpty() && !inputHeight.text.toString().isEmpty()
                && !inputWeight.text.toString().isEmpty() && !inputHoursExercise.text.toString().isEmpty()
                && !inputBirthDate.text.toString().isEmpty()
            ) {
                launch {
                    var actualUser: User = mainConnection!!.getUser(user!!)!!
                    actualUser.setName(inputUsername.text.toString())
                    actualUser.setWeight(inputWeight.text.toString().toInt())
                    actualUser.setHeight(inputHeight.text.toString().toFloat())
                    actualUser.setExercise(inputHoursExercise.text.toString().toInt())
                    var sexString: String = ""
                    if (maleOption.isChecked) {
                        sexString = "Male"
                    } else if (femaleOption.isChecked) {
                        sexString = "Female"
                    } else {
                        sexString = "Other"
                    }
                    actualUser.setSex(sexString)
                    actualUser.setHasInf(true)
                    actualUser.setAge(age)
                    actualUser.setMaxWater(17.0f)
                    mainConnection!!.setUser(actualUser)
                    //age
                    //maxWater
                    val principal = Intent(this@SignIn2Activity, TrueWeatherActivity::class.java)
                    finish()
                    startActivity(principal)
                    overridePendingTransition(R.anim.slide_up_to_down, R.anim.slide_stop)
                }
            } else {
                Toast.makeText(this, "Introduce all values", Toast.LENGTH_LONG).show()
                btnSignin.isClickable = true
            }
        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val returnLogin = Intent(this, LoginActivity::class.java)
        startActivity(returnLogin)
        finish()
        overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        val selectedDate = Calendar.getInstance()
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this, DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val selectedDateFormatted = format.format(selectedDate.time)
                inputBirthDate!!.setText(selectedDateFormatted)
                var actualTime = System.currentTimeMillis() / 1000
                var milisSelectedDate = selectedDate.timeInMillis / 1000;
                age = (actualTime - milisSelectedDate) / (3600 * 24 * 365)
            },
            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}

