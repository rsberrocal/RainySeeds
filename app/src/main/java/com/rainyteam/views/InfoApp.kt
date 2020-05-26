package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.rainyteam.controller.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class InfoApp : AppCompatActivity(), CoroutineScope, LifecycleObserver {

    val PREF_ID = "USER"
    val PREF_NAME = "USER_ID"
    var prefs: SharedPreferences? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.info_app)

        val buttonOK : FrameLayout = findViewById(R.id.buttonOK)
        buttonOK.setOnClickListener{
            val intent = Intent(this, GreenhouseActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_stop, R.anim.slide_stop)

        }
    }
}