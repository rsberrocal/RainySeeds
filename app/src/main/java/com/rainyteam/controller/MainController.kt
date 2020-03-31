package com.rainyteam.controller

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.rainyteam.model.Connection
import java.io.Serializable

//Necesita ser serializable para pasarlo a traves de activitys
class MainController : Serializable {

    var waterController = WaterController()
    var connection: Connection? = null

    init {
        connection = Connection()
    }

    fun getInstanceFirebaseAuth(): FirebaseAuth {
        return connection!!.mAuth()
    }

    fun getInstanceDatabase(): FirebaseDatabase {
        return connection!!.mDatabase()
    }
}