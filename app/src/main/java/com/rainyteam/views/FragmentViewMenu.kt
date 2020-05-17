package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.fragment_viewmenu.view.*
import kotlinx.coroutines.delay

class FragmentViewMenu : androidx.fragment.app.Fragment() {

    //shared
    val PREF_ID = "USER"
    var prefs: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        prefs = requireActivity().getSharedPreferences(PREF_ID, 0)
        //prefs!!.edit().putBoolean("NAV", false).apply()

        val viewActual = inflater.inflate(R.layout.fragment_viewmenu, container, false)

        viewActual.buttonDictionary.setOnClickListener { view ->
            requireActivity().finish()
            if (activity?.javaClass?.simpleName != EncyclopediaActivity::class.simpleName) {
                val principal = Intent(activity, EncyclopediaActivity::class.java)
                prefs!!.edit().putBoolean("NAV",true).apply()
                startActivity(principal)
                activity?.overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)
            }
        }

        viewActual.buttonGlass.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != MainWaterActivity::class.simpleName) {
                val principal = Intent(activity, MainWaterActivity::class.java)
                prefs!!.edit().putBoolean("NAV",true).apply()
                startActivity(principal)
                activity?.overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_stop)
            }
        }
        viewActual.buttonGreenhouse.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != GreenhouseActivity::class.simpleName) {
                val principal = Intent(activity, GreenhouseActivity::class.java)
                if (!activity!!.isTaskRoot){
                    activity!!.finish()
                }
                prefs!!.edit().putBoolean("NAV",true).apply()
                startActivity(principal)
                activity?.overridePendingTransition(R.anim.slide_down_to_up, R.anim.slide_stop)
            }
        }

        return viewActual
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}