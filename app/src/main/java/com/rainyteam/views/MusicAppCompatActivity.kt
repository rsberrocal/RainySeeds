package com.rainyteam.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.services.MusicService

open class MusicAppCompatActivity : AppCompatActivity() {

    var musicService : Intent? = null

    override fun onResume() {
        super.onResume()
        musicService = Intent(this, MusicService::class.java)
        startService(musicService)
    }

    override fun onStop() {
        super.onStop()
        stopService(musicService)
    }
}