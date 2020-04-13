package com.rainyteam.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rainyteam.controller.R
import kotlinx.android.synthetic.main.template_greenhouse_plant.view.*

class FragmentPlantGreenhouse : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.template_greenhouse_plant, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.plantGreenhouseImage.setOnClickListener {
            val intent = Intent(view.context, EncyclopediaDetailActivity::class.java)
            view.context.startActivity(intent)
        }
    }


}