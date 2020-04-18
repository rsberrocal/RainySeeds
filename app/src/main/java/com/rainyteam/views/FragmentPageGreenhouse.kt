package com.rainyteam.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rainyteam.controller.R
import com.rainyteam.model.Connection
import com.rainyteam.model.Plants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class FragmentPageGreenhouse : androidx.fragment.app.Fragment(), CoroutineScope {

    var mainConnection: Connection? = null

    private var mutableList: MutableList<Plants>? = null

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

        launch{
            mutableList = mainConnection!!.getAllPlants(null)

        }

        fun newInstance(mutableList: MutableList<String>) : FragmentPageGreenhouse {
            val args = Bundle()
            args.putString()
            val fragment = FragmentPageGreenhouse()
            fragment.arguments = args
            return fragment
        }

        return viewActual
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}