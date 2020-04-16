package com.rainyteam.views

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.rainyteam.services.MusicService
import java.lang.Integer.parseInt


class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val status = intent.getStringExtra("status")
        var music = Intent(context, MusicService::class.java)
        if (parseInt(status.toString()) === 0) {
            context.stopService(music)
        } else if (parseInt(status.toString()) === 1) {
            context.startService(music)
        } else if (parseInt(status.toString()) === 2) {
            context.stopService(music)
        }

    }
}
