package com.rainyteam.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import kotlinx.android.synthetic.main.main_water_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class MainWaterActivity : AppCompatActivity(), CoroutineScope {

    var mainConnection: Connection? = null

    var textWaterPercent : TextView? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_water_layout)

        this.mainConnection = Connection()

        this.textWaterPercent = findViewById(R.id.waterPercent)
        launch{
            //textWaterPercent!!.text = mainConnection.getUser().rainycoins
        }

        banner.setOnClickListener {
            val intent = Intent(this, UserInfoActivity::class.java)
            startActivity(intent)
        }
        waterButton.setOnClickListener {
            val intent = Intent(this, IntroduceWaterActivity::class.java)
            startActivity(intent)
        }
    }
}
