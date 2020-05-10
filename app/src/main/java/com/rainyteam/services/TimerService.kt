package com.rainyteam.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import java.lang.ClassCastException
import java.util.*
import kotlin.math.log

class TimerService : Service() {
    val TAG = "Timer"
    var counter: Int = 0
    var lastTime: Int = 0
    val PREF_NAME = "USER"
    val PREF_ID = "NEXT"
    var prefs: SharedPreferences? = null
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
        prefs = getSharedPreferences(PREF_NAME, 0)
        var aux = 0
        try {
            aux = prefs!!.getInt(PREF_ID, 0)
        } catch (E: ClassCastException) {
            var edit = prefs!!.edit()
            edit.putInt(PREF_ID, 0)
            edit.apply()
        }
        val runable = Runnable {
            var time = Calendar.getInstance().timeInMillis / 1000;
            var nextTime = time + 60; //next time will be 60 seconds more
            if (aux == 0) {
                var edit = prefs!!.edit()
                edit.putInt("NEXT", nextTime.toInt())
                edit.apply()
            }
            while (true) {
                time = Calendar.getInstance().timeInMillis / 1000;
                if (time > nextTime) {
                    logMessage("Time more than next: " + time)
                } else {
                    logMessage("Time less than next: " + time)
                }
                Thread.sleep(1000)
            }
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