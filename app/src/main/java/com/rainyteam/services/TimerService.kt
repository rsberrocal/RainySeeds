package com.rainyteam.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import kotlinx.coroutines.tasks.await
import java.lang.ClassCastException
import java.util.*
import kotlin.math.log

class TimerService : Service() {
    val TAG = "Timer"
    var counter: Int = 0
    var lastTime: Int = 0
    val PREF_NAME = "USER"
    val PREF_ID = "NEXT"
    var user = ""
    var prefs: SharedPreferences? = null
    lateinit var connection: Connection
    override fun onCreate() {
        logMessage("Starting")
        super.onCreate()
        connection = Connection()
        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")!!
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
            // todo matar a las plantas de forma progresiva,
            while (true) {
                time = Calendar.getInstance().timeInMillis / 1000;
                if (time > nextTime) {
                    logMessage("Updating")
                    prefs!!.edit().putInt("NEXT", time.toInt());
                    nextTime += 60
                    //Matamos plantas
                    connection.BDD.collection("User-Plants")
                        .whereEqualTo("userId", user)
                        .whereGreaterThan("status", 0)
                        .get()
                        .addOnSuccessListener { result ->

                            result.documents.forEach {
                                val plant = it.toObject(UserPlants::class.java)
                                connection.BDD.collection("Plants")
                                    .document(plant!!.plantId)
                                    .get()
                                    .addOnSuccessListener { detail ->
                                        var aux = detail.toObject(Plants::class.java)
                                        // todo cambiar batch por update de solo un document.
                                        val batch = connection.BDD.batch()
                                        batch.update(
                                            it.reference,
                                            "status",
                                            getDrying(aux!!, plant.status)
                                        )
                                        batch.commit()
                                        logMessage("update status: " + plant.plantId + " less 20")
                                    }
                            }
                        }

                    //logMessage("Time more than next: " + time)
                } else {
                    //logMessage("Time less than next: " + time)
                }
                Thread.sleep(1000)
            }
            //logMessage("Time in millis " + time)
        }
        val thread = Thread(runable)
        thread.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    fun getDrying(plant: Plants, status: Int): Int {
        var num = 0.0
        num = plant.getMoney() * 0.2
        if (status - num < 0) {
            return -1;
        }
        return (status - num).toInt()
    }

}