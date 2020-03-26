package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.fragment_viewmenu.view.*

class SelectionViewMenu : androidx.fragment.app.Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewActual = inflater.inflate(R.layout.fragment_viewmenu, container, false)

        viewActual.buttonDictionary.setOnClickListener { view ->
            if (activity?.javaClass?.simpleName != EncyclopediaActivity::class.simpleName) {
                val principal = Intent(activity, EncyclopediaActivity::class.java)
                startActivity(principal)
            }
        }

        viewActual.buttonGlass.setOnClickListener { view ->
            println(activity?.javaClass?.simpleName)
            if (activity?.javaClass?.simpleName != MainWaterActivity::class.simpleName) {
                val principal = Intent(activity, MainWaterActivity::class.java)
                startActivity(principal)
            }
        }
        viewActual.buttonGreenhouse.setOnClickListener { view ->
            println(activity?.javaClass?.simpleName)
            if (activity?.javaClass?.simpleName != GreenhouseActivity::class.simpleName) {
                val principal = Intent(activity, GreenhouseActivity::class.java)
                startActivity(principal)
            }
        }

        return viewActual
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}