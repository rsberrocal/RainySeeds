package com.rainyteam.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.*
import kotlin.math.log

class TimerService : Service() {
    val TAG = "Timer"
    var counter: Int = 0
    var lastTime: Int = 0

    override fun onCreate() {
        logMessage("Starting")
        super.onCreate()
    }

    override fun onDestroy() {
        logMessage("Destoying")
        super.onDestroy()
    }

    fun logMessage(message: String) {
        Log.d(TAG, message)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logMessage("Starting timer command")
        val runable = Runnable {
            var time = Calendar.getInstance().timeInMillis / 1000;
            logMessage("Time in millis " + time)
        }
        val thread = Thread(runable)
        thread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}