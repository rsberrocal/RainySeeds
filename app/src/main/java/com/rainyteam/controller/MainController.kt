package com.rainyteam.controller

import java.io.Serializable

//Necesita ser serializable para pasarlo a traves de activitys
class MainController : Serializable {

    var waterController = WaterController()

    init {

    }
}