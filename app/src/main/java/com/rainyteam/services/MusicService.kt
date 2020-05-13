package com.rainyteam.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import com.rainyteam.controller.R

class MusicService : Service() {

    var mplayer: MediaPlayer? = null

    //shared
    val PREF_ID = "USER"
    var prefs: SharedPreferences? = null


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MUSIC", "Starting Command")
        //var res = resources.getIdentifier("rainysong.mp3", "raw", packageName)
        mplayer = MediaPlayer.create(this, R.raw.rainysong)
        mplayer?.isLooping = true
        mplayer?.start()
        var audio:AudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, 10, 0)
        prefs = getSharedPreferences(PREF_ID, 0)
        //Guardamos el estado global del play a true
        prefs!!.edit().putBoolean("PLAY", true).apply()
        return START_STICKY
    }

    fun onUnBind(arg0: Intent?): IBinder? {
        // TO DO Auto-generated method
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mplayer?.stop()
        mplayer?.release()
        prefs = getSharedPreferences(PREF_ID, 0)
        //Guardamos el estado global del play a false
        prefs!!.edit().putBoolean("PLAY", false).apply()
    }
}
