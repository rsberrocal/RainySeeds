package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class MusicAppCompatActivity : AppCompatActivity(), CoroutineScope {

    var musicService: Intent? = null

    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null


    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onResume() {
        super.onResume()
        /*musicService = Intent(this, MusicService::class.java)
        launch {
            var database = Connection()
            var prefs = getSharedPreferences(PREF_ID, 0)
            var usermail = prefs.getString(PREF_NAME, "")
            if (usermail != "") {
                var user: User = database.getUser(usermail!!)!!
                if (user.music) {
                    startService(musicService)
                }
            }
        }*/
    }

    override fun onStop() {
        super.onStop()
        /*musicService = Intent(this, MusicService::class.java)
        launch {
            var database = Connection()
            var prefs = getSharedPreferences(PREF_NAME, 0)
            var user: User = database.getUser(prefs.getString(PREF_ID, "")!!)!!
            if (user.music) {
                stopService(musicService)
            }
        }*/
    }
}