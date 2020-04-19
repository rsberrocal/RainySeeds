package com.rainyteam.views

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import com.rainyteam.model.UserPlants
import kotlinx.android.synthetic.main.fragment_plants_page.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class FragmentPageGreenhouse(
    position: Int
) : androidx.fragment.app.Fragment(), CoroutineScope {

    var mainConnection: Connection? = null

    val PREF_NAME = "USER"
    var prefs: SharedPreferences? = null
    var user: String? = ""

    private var mutableList: MutableList<Plants>? = null
    val pos: Int = position;

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewActual = inflater.inflate(R.layout.fragment_plants_page, container, false)

        this.mainConnection = Connection()
        prefs = context?.getSharedPreferences(PREF_NAME, 0)
        this.user = prefs!!.getString(PREF_NAME, "")

        launch {
            mutableList = mainConnection!!.getAllPlants(null)
            //mutableList = user?.let { mainConnection!!.getUserPlantsAlive(it) }

            val drawableName1 : String? = mutableList!![pos*9].getPotImagePlant()
            val resID1: Int = resources.getIdentifier(drawableName1, "drawable", context?.packageName)
            plantR1C1!!.setImageResource(resID1)

            val drawableName2 : String? = mutableList!![pos*9+1].getPotImagePlant()
            val resID2: Int = resources.getIdentifier(drawableName2, "drawable", context?.packageName)
            plantR1C2!!.setImageResource(resID2)

            val drawableName3 : String? = mutableList!![pos*9+2].getPotImagePlant()
            val resID3: Int = resources.getIdentifier(drawableName3, "drawable", context?.packageName)
            plantR1C3!!.setImageResource(resID3)

        }

        return viewActual
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}