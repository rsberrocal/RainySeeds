package com.rainyteam.services

import android.app.IntentService
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import com.rainyteam.patterns.Observable
import com.rainyteam.patterns.Observer
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.ClassCastException
import java.lang.Runnable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.timerTask
import kotlin.coroutines.CoroutineContext
import kotlin.math.log

class TimerService : Service(), CoroutineScope {
    val TAG = "Timer"
    val PREF_NAME = "USER"
    val PREF_NEXT = "NEXT"
    val PREF_ACTUAL = "ACTUAL"
    var user = ""
    var prefs: SharedPreferences? = null
    var actualTime: Long = 0
    var nextTime: Long = 0
    lateinit var mainTimer: Timer
    lateinit var timerTask: TimerTask
    //in seconds
    //val WAIT_TIME = 3600
    val WAIT_TIME = 60
    lateinit var connection: Connection

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate() {
        super.onCreate()
        logMessage("Starting")
        connection = Connection()
        prefs = getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString("USER_ID", "")!!
    }

    override fun onDestroy() {
        logMessage("Destroying")
        //this.mainThread.interrupt()
        this.mainTimer.cancel()
        this.mainTimer.purge()

        super.onDestroy()
    }

    fun logMessage(message: String) {
        Log.d(TAG, message)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logMessage("Starting timer command")
        //Miro si hay un next guardado de otro momento
        var savedNextTime = 0
        try {
            savedNextTime = prefs!!.getInt(PREF_NEXT, 0)
        } catch (E: ClassCastException) {
            //De no existir lo creo poniendo un 0
            prefs!!.edit().putInt(PREF_NEXT, 0).apply()
        }
        //Consigo el tiempo actual en segundos (Epoch time)
        actualTime = Calendar.getInstance().timeInMillis / 1000;

        //El tiempo a actualizar sera el tiempo actual mas lo que hay que esperar (1 hora)
        nextTime = actualTime + WAIT_TIME;
        //Si el next guardado es 0, pongo como next el nuevo
        if (savedNextTime == 0) {
            logMessage("Saved next time was not set")
            prefs!!.edit().putInt(PREF_NEXT, nextTime.toInt()).apply()
        } else if (actualTime > savedNextTime) {//miramos si el next guardado ha sido superado, por lo tanto actualizamos
            // todo calcular cuanto tiempo ha pasado desde la ultima vez. mirar cuantas horas y cuantos updates hay que hacer
            //update
            prefs!!.edit().putInt(PREF_NEXT, nextTime.toInt()).apply()
            updatePlants()
            logMessage("Update plants with saved nexttime")
        }
        // todo matar a las plantas de forma progresiva,
        this.mainTimer = Timer()
        this.timerTask = timerTask {
            actualTime = Calendar.getInstance().timeInMillis / 1000;
            if (actualTime > nextTime) {
                logMessage("Updating")
                nextTime = actualTime + WAIT_TIME;
                prefs!!.edit().putInt("NEXT", nextTime.toInt()).apply();
                //Matamos plantas
                //update plantas y dinero usuario
                updatePlants()
                logMessage("update plants")
                //logMessage("Time more than next: " + time)
            }
        }
        this.mainTimer.schedule(this.timerTask,1000)
        //se crea el thread runnable
        /*val runable = Runnable {

            while (true) {

                //logMessage("Time less than next: " + time)
                //paramos el hilo cada segundo
                try {
                    launch {
                        delay(1,)
                    }
                }catch (e:InterruptedException){
                    println("Interrumping")
                    logMessage(e.message!!)
                }

            }
        }*/
        //Guardamos el hilo para detenerlo
        //this.mainThread = Thread(runable)
        //iniciamos el timer
        //mainThread.start()
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

    fun updatePlants() {
        launch {
            var userData = connection.getUser(user)!!
            var moneyToAdd = userData.getRainyCoins();
            connection.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThan("status", -1)
                .get()
                .addOnSuccessListener { result ->
                    var items = result.documents.size
                    logMessage("Number of items" + items)
                    var count = 0;
                    result.documents.forEach {
                        val plant = it.toObject(UserPlants::class.java)
                        connection.BDD.collection("Plants")
                            .document(plant!!.plantId)
                            .get()
                            .addOnSuccessListener { detail ->
                                var aux = detail.toObject(Plants::class.java)!!
                                //dinero que recibe el usuario

                                if (detail.id == "Cactus") {
                                    moneyToAdd += 5
                                } else {
                                    moneyToAdd += (aux.getMoney() * 10) / 100
                                }

                                it.reference.update(
                                    "status",
                                    getDrying(aux, plant.status)
                                )
                                logMessage("update status: " + plant.plantId + " less 20")
                                count++
                                if (items == count) {
                                    logMessage("is Last element")
                                    //sendMessage()
                                    //todo mirar si lo del dinero es de esa forma
                                    logMessage("Money adding " + moneyToAdd)
                                    connection.BDD.collection("Users")
                                        .document(user)
                                        .update("rainyCoins", moneyToAdd)
                                        .addOnSuccessListener { result ->
                                            sendMessage()
                                        }
                                    //update views
                                }
                            }
                    }
                }
        }

    }

    fun sendMessage() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("Timer"))
    }

}