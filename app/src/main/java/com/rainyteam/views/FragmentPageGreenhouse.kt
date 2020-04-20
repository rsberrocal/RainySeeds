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

    private var mutableList: MutableList<UserPlants>? = null
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
        this.user = prefs!!.getString("USER_ID", "")

        launch {
            mutableList = user?.let {
                mainConnection!!.getUserPlantsAlive(it)
            }
            if (mutableList!!.size >= pos * 9) {
                val auxPlant1: Plants? = mainConnection!!.getPlant(mutableList!![pos * 9].plantId)
                val drawableName1: String? = auxPlant1!!.getPotImagePlant()
                val resID1: Int =
                    resources.getIdentifier(drawableName1, "drawable", context?.packageName)
                plantR1C1!!.setImageResource(resID1)
            }
            if (mutableList!!.size >= pos * 9 + 1) {
                val auxPlant2: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 1].plantId)
                val drawableName2: String? = auxPlant2!!.getPotImagePlant()
                val resID2: Int =
                    resources.getIdentifier(drawableName2, "drawable", context?.packageName)
                plantR1C2!!.setImageResource(resID2)
            }
            if (mutableList!!.size >= pos * 9 + 2) {
                val auxPlant3: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 2].plantId)
                val drawableName3: String? = auxPlant3!!.getPotImagePlant()
                val resID3: Int =
                    resources.getIdentifier(drawableName3, "drawable", context?.packageName)
                plantR1C3!!.setImageResource(resID3)
            }
            if (mutableList!!.size >= pos * 9 + 3) {
                val auxPlant4: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 3].plantId)
                val drawableName4: String? = auxPlant4!!.getPotImagePlant()
                val resID4: Int =
                    resources.getIdentifier(drawableName4, "drawable", context?.packageName)
                plantR2C1!!.setImageResource(resID4)
            }
            if (mutableList!!.size >= pos * 9 + 4) {
                val auxPlant5: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 4].plantId)
                val drawableName5: String? = auxPlant5!!.getPotImagePlant()
                val resID5: Int =
                    resources.getIdentifier(drawableName5, "drawable", context?.packageName)
                plantR2C2!!.setImageResource(resID5)
            }/*
            if (mutableList!!.size >= pos * 9 + 5) {
                val auxPlant6: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 5].plantId)
                val drawableName6: String? = auxPlant6!!.getPotImagePlant()
                val resID6: Int =
                    resources.getIdentifier(drawableName6, "drawable", context?.packageName)
                plantR2C3!!.setImageResource(resID6)
            }
            if (mutableList!!.size >= pos * 9 + 6) {
                val auxPlant7: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 6].plantId)
                val drawableName7: String? = auxPlant7!!.getPotImagePlant()
                val resID7: Int =
                    resources.getIdentifier(drawableName7, "drawable", context?.packageName)
                plantR3C1!!.setImageResource(resID7)
            }

            if (mutableList!!.size >= pos * 9 + 7) {
                val auxPlant8: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 7].plantId)
                val drawableName8: String? = auxPlant8!!.getPotImagePlant()
                val resID8: Int =
                    resources.getIdentifier(drawableName8, "drawable", context?.packageName)
                plantR3C2!!.setImageResource(resID8)
            }
            if (mutableList!!.size >= pos * 9 + 8) {
                val auxPlant9: Plants? =
                    mainConnection!!.getPlant(mutableList!![(pos * 9) + 8].plantId)
                val drawableName9: String? = auxPlant9!!.getPotImagePlant()
                val resID9: Int =
                    resources.getIdentifier(drawableName9, "drawable", context?.packageName)
                plantR3C3!!.setImageResource(resID9)
            }*/
        }
        return viewActual
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}