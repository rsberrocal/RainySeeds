package com.androdocs.weatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.User
import com.rainyteam.services.MusicService
import com.rainyteam.views.GreenhouseActivity
import com.rainyteam.views.LoginActivity
import kotlinx.android.synthetic.main.activity_true_weather.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import android.annotation.SuppressLint
import android.os.Looper
import com.google.android.gms.location.*
import io.opencensus.trace.export.SampledSpanStore

import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch

import kotlin.coroutines.CoroutineContext

class TrueWeatherActivity : AppCompatActivity(), CoroutineScope {
    var mainConnection: Connection? = null
    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = null
    var temp: Float = 0.0f
    val PERMISSION_ID = 42
    var LAT= 0.0
    var LON = 0.0
    val CITY: String = "Barcelona"
    val API: String = "8118ed6ee68db2debfaaa5a44c832918"
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        setContentView(R.layout.activity_true_weather)
        this.mainConnection = Connection()

        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")
        weatherTask().execute()
        reCalculateUserMaxWater()
        falseGreenHouseLayout.setOnClickListener{
            setContentView(R.layout.greenhouse_layout)
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    fun reCalculateUserMaxWater() {
        launch {
            var actualUser: User = mainConnection!!.getUser(user!!)!!
            actualUser.setMaxWater(temp)
            mainConnection!!.setUser(actualUser)
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val returnTrueWeatherActivity = Intent(this, GreenhouseActivity::class.java)
        startActivity(returnTrueWeatherActivity)
        finish()
        overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* Showing the ProgressBar, Making the main design GONE */

        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=$LAT&lon=$LON&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response

        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting JSON returns from the API */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                temp = main.getString("temp").toFloat()
                var message:String =""
                if(temp<14){
                    message="Today is a cold day, you shouldn't worry about drinking more water as usual."
            }
                else if(temp>=14 && temp<27){
                    message="Today it's a common day, not cold, not hot. Just drink the same water as usual."
                }
                else{
                    message="Today it's a hot day, you should drink more water than usual. "
                }
                weatherText.text = message

            }
            catch(e: java.lang.Exception){
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        LAT = location.latitude
                        LON = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            LAT = mLastLocation.latitude
            LON = mLastLocation.longitude
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }
}