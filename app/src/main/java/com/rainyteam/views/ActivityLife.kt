package com.rainyteam.views

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class ActivityLife : Application.ActivityLifecycleCallbacks {
    private val resumed = 0
    private val paused = 0
    private var currentActivity: String? = null
    lateinit  var context: android.content.Context

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity.javaClass.simpleName
    }

    fun getCurrentActivity(): String? {
        return currentActivity
    }

    override fun onActivityStarted(activity: Activity?) {}
    override fun onActivityResumed(activity: Activity?) {
        send_status(1)
    }

    override fun onActivityPaused(activity: Activity?) {
        send_status(0)
    }

    private fun send_status(status_counter: Int) {
        val intent = Intent("status")
        intent.putExtra("status", status_counter.toString())
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    override fun onActivityStopped(activity: Activity?) {}
    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {}
    override fun onActivityDestroyed(activity: Activity?) {
        send_status(2)
    }
}