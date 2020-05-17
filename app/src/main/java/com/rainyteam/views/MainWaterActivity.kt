package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.History
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainWaterActivity : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    var mainConnection: Connection? = null

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    var firstNav = false

    var userName: String? = null
    var textWaterPercent: TextView? = null

    val storage = FirebaseStorage.getInstance().reference
    lateinit var userRef: StorageReference

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        setContentView(R.layout.main_water_layout)

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, "")

        this.mainConnection = Connection()
        getWater(userName)
        setUser(userName)

        this.textWaterPercent = findViewById(R.id.waterPercent)


        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
            finish()
        }
        waterButton.setOnClickListener {
            val intent = Intent(this, IntroduceWaterActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)            
        }

        var imgUser = findViewById(R.id.userIcon) as ImageView

        userRef = storage.child("$userName.jpg")
        //maxDownload is 1MB
        userRef.getBytes(1024 * 1024)
            .addOnSuccessListener {
                Log.d("Connection", "Has image")
                val image = BitmapFactory.decodeByteArray(it, 0, it.size)
                imgUser.setImageBitmap(getCroppedBitmap(image))
                //imgUser.setImageBitmap(image)
            }
            .addOnFailureListener {
                Log.d("Connection", it.message)
            }

        //prefs!!.edit().putBoolean("NAV", false).commit()
        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
            /** Delay para definir que no es navegacion al crear vista **/
            //delay(1000)
            //prefs!!.edit().putBoolean("NAV", false).apply()
        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        if (!isTaskRoot) {
            prefs!!.edit().putBoolean("NAV", true).apply()
        }
        prefs!!.edit().putBoolean("NAV", false).apply()
    }

    fun isActivityVisible(): String {
        return ProcessLifecycleOwner.get().lifecycle.currentState.name
    }

    fun setUser(user: String?) {
        launch {
            var actualUser = mainConnection!!.getUser(user!!)
            userNameText!!.text = actualUser?.getUsername()
            userAge!!.text = actualUser?.getAge().toString() + " years"
            userWeight!!.text = actualUser?.getWeight().toString() + " kg"
            emailText!!.text = actualUser?.getEmail().toString()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getWater(user: String?) {

        launch {
            var actualHistory: History? = mainConnection!!.getHistory(user!!)
            var cal: Calendar = Calendar.getInstance()
            var day = cal.get(Calendar.DAY_OF_WEEK)
            var actualUserWater: Float = 30f
            when (day) {
                Calendar.SUNDAY -> actualUserWater = actualHistory!!.sunday
                Calendar.MONDAY -> actualUserWater = actualHistory!!.monday
                Calendar.TUESDAY -> actualUserWater = actualHistory!!.tuesday
                Calendar.WEDNESDAY -> actualUserWater = actualHistory!!.wednesday
                Calendar.THURSDAY -> actualUserWater = actualHistory!!.thursday
                Calendar.FRIDAY -> actualUserWater = actualHistory!!.friday
                Calendar.SATURDAY -> actualUserWater = actualHistory!!.saturday
            }
            setWaterImage(actualUserWater)
        }

    }

    fun setWaterImage(actualUserWater: Float) {
        when {
            actualUserWater <= 5 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_empty)
            )
            actualUserWater <= 10 && actualUserWater > 5 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_5)
            )
            actualUserWater <= 20 && actualUserWater > 10 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_10)
            )
            actualUserWater <= 30 && actualUserWater > 20 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_20)
            )
            actualUserWater <= 40 && actualUserWater > 30 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_30)
            )
            actualUserWater <= 50 && actualUserWater > 40 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_40)
            )
            actualUserWater <= 60 && actualUserWater > 50 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_50)
            )
            actualUserWater <= 70 && actualUserWater > 60 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_60)
            )
            actualUserWater <= 80 && actualUserWater > 70 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_70)
            )
            actualUserWater <= 90 && actualUserWater > 80 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_80)
            )
            actualUserWater > 90 -> waterButton.setImageDrawable(
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.bottle_90)
            )
        }
        textWaterPercent!!.text = actualUserWater.toInt().toString() + "%"

    }


    fun getCroppedBitmap(bitmap: Bitmap): Bitmap {
        val output = Bitmap.createBitmap(
            bitmap.width,
            bitmap.height, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(output)
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        paint.setAntiAlias(true)
        canvas.drawARGB(0, 0, 0, 0)
        paint.setColor(color)
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(
            (bitmap.width / 2).toFloat(), (bitmap.height / 2).toFloat(),
            (bitmap.width / 2).toFloat(), paint
        )
        paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
        canvas.drawBitmap(bitmap, rect, rect, paint)
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
//return _bmp;
        return output
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onAppBackgrounded() {
        //App in background

        Log.e("MUSIC", "************* backgrounded main water")
        Log.e("MUSIC", "************* ${isActivityVisible()}")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav){
            this.firstNav = true
        }else{
            Log.e("MUSIC", "************* foregrounded main water")
            Log.e("MUSIC", "************* ${isActivityVisible()}")
            // App in foreground
            Log.d("MUSIC", "ON RESTART MainWaterActivity")
            //Se crea el intent para iniciarlo
            val musicService = Intent(this, MusicService::class.java)
            val timerService = Intent(this, TimerService::class.java)
            //var musicPlay = prefs!!.getBoolean("PLAY", false)
            //val isNav = prefs!!.getBoolean("NAV", false);
            //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
            launch {
                var auxUser: User = mainConnection!!.getUser(userName!!)!!
                if (auxUser.music) {
                    Log.d("MUSIC", "STARTING ON RESTART")
                    startService(musicService)
                }
                startService(timerService)
            }
        }
    }

}


