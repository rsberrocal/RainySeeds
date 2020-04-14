package com.rainyteam.services

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.provider.Settings

class MusicService : Service() {

    var mplayer : MediaPlayer? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mplayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI)
        mplayer?.isLooping = true //arreglar
        mplayer?.start()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        mplayer?.stop()
    }
}
