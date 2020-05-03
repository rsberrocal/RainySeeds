package com.rainyteam.views

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.android.synthetic.main.sign_in_2_layout.*
import kotlinx.android.synthetic.main.user_info_layout.*
import kotlinx.android.synthetic.main.user_info_layout.view.*
import kotlinx.android.synthetic.main.user_info_layout.view.textAge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.coroutines.CoroutineContext


class SignIn2Activity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    //shared
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = null
    var format = SimpleDateFormat("dd MMM, YYY", Locale.US)
    var age: Long = 0
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
            age = showDatePickerDialog();
        }
        btnSignin.setOnClickListener {
            launch {
                var actualUser: User? = mainConnection!!.getUser(user!!)
                actualUser!!.setName(inputUsername.text.toString())
                actualUser!!.setWeight(inputWeight.text.toString().toInt())
                actualUser!!.setHeight(inputHeight.text.toString().toFloat())
                actualUser!!.setExercise(inputHoursExercise.text.toString().toInt())
                var sexString: String = ""
                if (maleOption.isChecked) {
                    sexString = "Male"
                } else if (femaleOption.isChecked) {
                    sexString = "Female"
                } else {
                    sexString = "Other"
                }
                actualUser!!.setSex(sexString)
                actualUser!!.setHasInf(true)
                actualUser!!.setAge(age)
                mainConnection!!.setUser(actualUser)
                //age
                //maxWater
            }

            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog(): Long {
        val selectedDate = Calendar.getInstance()
        val now = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this, DatePickerDialog.OnDateSetListener() { view, year, month, dayOfMonth ->
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val selectedDateFormatted = format.format(selectedDate.time)
                inputBirthDate!!.setText(selectedDateFormatted)
            },
            now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
        val currentDate = LocalDateTime.now()
        selectedDate.toInstant()

        //return ChronoUnit.YEARS.between(selectedDate.toInstant(), currentDate)


        return 4;
    }
}

