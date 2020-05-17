package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import com.rainyteam.services.TimerService
import kotlinx.android.synthetic.main.user_info_layout.*
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.CoroutineContext


class UserInfoActivity() : AppCompatActivity(), CoroutineScope, LifecycleObserver {
    val mAuth = FirebaseAuth.getInstance()

    var mainConnection: Connection? = null

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null
    var enableModify: Boolean = false
    var firstNav = false

    //Camara
    val REQUEST_IMAGE_CAPTURE = 1
    val storage = FirebaseStorage.getInstance().reference
    lateinit var userRef: StorageReference


    var userName: String? = ""
    val entries = ArrayList<Entry>()
    lateinit var imgUser: ImageView

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        setContentView(R.layout.user_info_layout)

        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_ID, 0)
        this.userName = prefs!!.getString(PREF_NAME, null)

        imgUser = findViewById(R.id.imgProfile) as ImageView
        imgUser.setOnClickListener {
            openCamera()
        }

        userRef = storage.child("$userName.jpg")
        //maxDownload is 1MB
        userRef.getBytes(1024 * 1024)
            .addOnSuccessListener {
                Log.d("Connection", "Has image")
                val image = BitmapFactory.decodeByteArray(it,0,it.size)
                imgUser.setImageBitmap(getCroppedBitmap(image))
            }
            .addOnFailureListener {
                Log.d("Connection", it.message)
            }



        setUser(userName)
        setUserStatics(userName)
        userBackArrowButton.setOnClickListener {
            val intent = Intent(this, MainWaterActivity::class.java)
            startActivity(intent)
            prefs!!.edit().putBoolean("NAV", true).apply()
            //overridePendingTransition(R.anim.slide_down_to_up, R.anim.slide_stop)
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
            finish()
        }
        btnModify.setOnClickListener{
            if(enableModify){
                acceptValues()
                enableModify=false
            }
            else{
                modifyView()
                enableModify=true
            }
        }
        logoutBtn.setOnClickListener {
            prefs!!.edit().remove(PREF_NAME).apply()
            var music = Intent(this, MusicService::class.java)
            var timer = Intent(this, TimerService::class.java)
            stopService(music)
            stopService(timer)
            mAuth.signOut()
            finish()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)

        }



        //prefs!!.edit().putBoolean("NAV", false).commit()

        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
            /** Delay para definir que no es navegacion al crear vista **/
            delay(1000)
            prefs!!.edit().putBoolean("NAV", false).commit()
        }
    }

    //Funcion que se ejecuta al tirar atras
    override fun onBackPressed() {
        super.onBackPressed()
        val returnUserInfoActivity = Intent(this, MainWaterActivity::class.java)
        startActivity(returnUserInfoActivity)
        finish()
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
    }

    fun setUser(user: String?) {
        launch {
            var actualUser = mainConnection!!.getUser(user!!)
            textName!!.text = actualUser?.getUsername()
            textAge!!.text = actualUser?.getAge().toString() + " years"
            textWeight!!.text = actualUser?.getWeight().toString() + " kg"
            textEmail!!.text = actualUser?.getEmail()
            textHeight!!.text = actualUser?.getHeight().toString() + " cm"
            textSex!!.text = actualUser?.getSex()
            textExercise!!.text = actualUser?.getExercise().toString() + " hours"
            ageEdit!!.text = Editable.Factory.getInstance().newEditable(actualUser?.getAge().toString())
            weightEdit!!.text = Editable.Factory.getInstance().newEditable(actualUser?.getWeight().toString())
            heightEdit!!.text = Editable.Factory.getInstance().newEditable(actualUser?.getHeight().toString())
            exerciseEdit!!.text = Editable.Factory.getInstance().newEditable(actualUser?.getExercise().toString())
            sexEdit!!.text = Editable.Factory.getInstance().newEditable(actualUser?.getSex().toString())
        }
    }

    fun setUserStatics(user: String?) {
        launch {
            var userHistory = mainConnection?.getHistory(user!!)
            if (userHistory != null) {
                entries.add(Entry(1f, userHistory.monday))
                entries.add(Entry(2f, userHistory.tuesday))
                entries.add(Entry(3f, userHistory.wednesday))
                entries.add(Entry(4f, userHistory.thursday))
                entries.add(Entry(5f, userHistory.friday))
                entries.add(Entry(6f, userHistory.saturday))
                entries.add(Entry(7f, userHistory.sunday))

                val dataSet = LineDataSet(entries, "Water Percentage")
                dataSet.setDrawValues(false)
                dataSet.setDrawFilled(true)
                dataSet.lineWidth = 3f
                dataSet.fillColor = R.color.gray
                dataSet.fillAlpha = R.color.colorPrimary

                lineChart.xAxis.labelRotationAngle = 0f

                lineChart.data = LineData(dataSet)

                lineChart.axisRight.isEnabled = false
                lineChart.xAxis.axisMaximum = 7f

                lineChart.setTouchEnabled(true)
                lineChart.setPinchZoom(true)


                lineChart.description.text = "Days"
                lineChart.setNoDataText("No forex yet!")


                lineChart.animateX(1800, Easing.EaseInExpo)

            }
        }
    }

    fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            var imageBitmap = data!!.extras!!.get("data") as Bitmap
            imageBitmap = getCroppedBitmap(imageBitmap)
            imgUser.setImageBitmap(imageBitmap)
            //Saving image on database
            //var userImageRef = storage.child("images/$userName.jpg")

            val imageRef = storage.child("$userName.jpg")
            /*userRef.name.equals(userImageRef.name)
            userRef.path.equals(userImageRef.path)*/

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = imageRef.putBytes(data)
            uploadTask.addOnSuccessListener {
                Log.d("Connection", "Imagen subida correctamente")
            }


        }
        super.onActivityResult(requestCode, resultCode, data)
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

        Log.e("MUSIC", "************* backgrounded user info")

        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        stopService(musicService)
        stopService(timerService)

    }
    fun acceptValues(){
        if(!ageEdit.text.toString().isEmpty() &&
            !weightEdit.text.toString().isEmpty() &&
            !heightEdit.text.toString().isEmpty() &&
            !sexEdit.text.toString().isEmpty() &&
            !exerciseEdit.text.toString().isEmpty() ) {

            ageEdit.visibility = View.INVISIBLE
            exerciseEdit.visibility = View.INVISIBLE
            heightEdit.visibility = View.INVISIBLE
            sexEdit.visibility = View.INVISIBLE
            weightEdit.visibility = View.INVISIBLE
            textAge!!.visibility = View.VISIBLE
            textWeight!!.visibility = View.VISIBLE
            textHeight!!.visibility = View.VISIBLE
            textSex!!.visibility = View.VISIBLE
            textExercise!!.visibility = View.VISIBLE
            btnModify.setImageResource(R.drawable.icon_pencil)
            launch {
                var actualUser = mainConnection!!.getUser(userName!!)
                actualUser!!.setAge(ageEdit.text.toString().toLong())
                actualUser!!.setSex(sexEdit.text.toString())
                actualUser!!.setWeight(weightEdit.text.toString().toInt())
                actualUser!!.setHeight(heightEdit.text.toString().toFloat())
                actualUser!!.setExercise(exerciseEdit.text.toString().toInt())
                actualUser!!.setMaxWater(17.0f)
                mainConnection!!.setUser(actualUser)
                setUser(userName)
            }
        }
        else{
            Toast.makeText(this, "Introduce all values", Toast.LENGTH_LONG).show()
        }
    }
    fun modifyView(){
        ageEdit.visibility = View.VISIBLE
        exerciseEdit.visibility = View.VISIBLE
        heightEdit.visibility = View.VISIBLE
        sexEdit.visibility = View.VISIBLE
        weightEdit.visibility = View.VISIBLE
        textAge!!.visibility = View.INVISIBLE
        textWeight!!.visibility = View.INVISIBLE
        textHeight!!.visibility = View.INVISIBLE
        textSex!!.visibility = View.INVISIBLE
        textExercise!!.visibility = View.INVISIBLE
        btnModify.setImageResource(R.drawable.tick_icon)

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onAppForegrounded() {
        if (!this.firstNav){
            this.firstNav = true
        }else{
            Log.e("MUSIC", "************* foregrounded user info")
            // App in foreground
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
