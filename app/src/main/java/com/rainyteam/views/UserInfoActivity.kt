package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.services.MusicService
import kotlinx.android.synthetic.main.user_info_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class UserInfoActivity() : AppCompatActivity(), CoroutineScope{
    val mAuth = FirebaseAuth.getInstance()

    var mainConnection: Connection? = null

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var userName: String? = ""
    val entries = ArrayList<Entry>()

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_info_layout)

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, null)
        setUser(userName)
        setUserStatics(userName)
        userBackArrowButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_down_to_up, R.anim.slide_stop)
        }

        logoutBtn.setOnClickListener {
            prefs!!.edit().remove(PREF_NAME).apply()
            var music = Intent(this, MusicService::class.java)
            stopService(music)
            mAuth.signOut()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)

        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
    }

    fun setUser(user: String?){
        launch {
            var actualUser = mainConnection!!.getUser(user!!)
            textName!!.text = actualUser?.getUsername()
            textAge!!.text =  actualUser?.getAge().toString() + " years"
            textWeight!!.text = actualUser?.getWeight().toString() + " kg"
            textEmail!!.text = actualUser?.getEmail()
            textHeight!!.text = actualUser?.getHeight().toString() + " cm"
            textSex!!.text = actualUser?.getSex()
            textExercise!!.text = actualUser?.getExercise().toString() + " hours"
        }
    }
    fun setUserStatics(user: String?){
        launch {
            var userHistory = mainConnection?.getHistory(user!!)
            if (userHistory != null) {
                entries.add(Entry(1f,userHistory.monday))
                entries.add(Entry(2f,userHistory.tuesday))
                entries.add(Entry(3f,userHistory.wednesday))
                entries.add(Entry(4f,userHistory.thursday))
                entries.add(Entry(5f,userHistory.friday))
                entries.add(Entry(6f,userHistory.saturday))
                entries.add(Entry(7f,userHistory.sunday))
                val dataSet= LineDataSet(entries, "Float")
                dataSet.setDrawValues(false)
                dataSet.setDrawFilled(true)
                dataSet.lineWidth = 3f
                dataSet.fillColor = R.color.gray
                dataSet.fillAlpha = R.color.colorPrimary

                lineChart.xAxis.labelRotationAngle = 0f

                lineChart.axisRight.isEnabled = false
                lineChart.xAxis.axisMaximum = 100f

                lineChart.setTouchEnabled(true)
                lineChart.setPinchZoom(true)


                lineChart.description.text = "Days"
                lineChart.setNoDataText("No forex yet!")


                lineChart.animateX(1800, Easing.EaseInExpo)

            }
        }
    }



}
