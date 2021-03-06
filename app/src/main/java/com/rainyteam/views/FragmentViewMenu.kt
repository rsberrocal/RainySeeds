package com.rainyteam.views

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.fragment_viewmenu.view.*

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

        val viewActual = inflater.inflate(R.layout.fragment_viewmenu, container, false)

        viewActual.buttonDictionary.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != EncyclopediaActivity::class.simpleName) {
                val principal = Intent(activity, EncyclopediaActivity::class.java)
                requireActivity().finish()
                startActivity(principal)
                activity?.overridePendingTransition(R.anim.slide_left_to_right, R.anim.slide_stop)
            }
        }

        viewActual.buttonGlass.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != MainWaterActivity::class.simpleName) {
                val principal = Intent(activity, MainWaterActivity::class.java)
                requireActivity().finish()
                startActivity(principal)
                activity?.overridePendingTransition(R.anim.slide_right_to_left, R.anim.slide_stop)
            }
        }
        viewActual.buttonGreenhouse.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != GreenhouseActivity::class.simpleName) {
                val principal = Intent(activity, GreenhouseActivity::class.java)
                if (!requireActivity().isTaskRoot){
                    requireActivity().finish()
                }
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