package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.*
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
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


class UserInfoActivity() : AppCompatActivity(), CoroutineScope {
    val mAuth = FirebaseAuth.getInstance()

    var mainConnection: Connection? = null

    //shared
    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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





        launch {
            //textWaterPercent!!.text = mainConnection.getUser(userName)
            /** Delay para definir que no es navegacion al crear vista **/
            delay(1000)
            prefs!!.edit().putBoolean("NAV", false).apply()
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

    override fun onStop() {
        super.onStop()
        //Se crea el intent para pararlo
        val musicService = Intent(this, MusicService::class.java)

        val timerService = Intent(this, TimerService::class.java)

        val isNav = prefs!!.getBoolean("NAV", false);
        //Se mira si es una navegacion, de no serla es un destroy de app, se apaga la musica
        if (!isNav) {
            //De ser un destroy se detiene
            stopService(musicService)
            stopService(timerService)
        }
    }

    //Viene de un destroy
    override fun onRestart() {
        super.onRestart()
        Log.d("MUSIC", "ON RESTART GREENHOUSE")
        //Se crea el intent para iniciarlo
        val musicService = Intent(this, MusicService::class.java)
        val timerService = Intent(this, TimerService::class.java)

        var musicPlay = prefs!!.getBoolean("PLAY", false)
        //Solo se inicia si la musica ha parado y si el usuario tiene habilitado el check
        launch {
            var auxUser: User = mainConnection!!.getUser(userName!!)!!
            if (auxUser.music && !musicPlay) {
                Log.d("MUSIC", "STARTING ON RESTART")
                startService(musicService)
            }
            startService(timerService)
        }
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


}
