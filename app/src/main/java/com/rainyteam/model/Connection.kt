package com.rainyteam.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class Connection {
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val BDD = FirebaseFirestore.getInstance()

    //Singleton, creem una nova instància si no hi ha cap creada anteriorment.

    fun mAuth(): FirebaseAuth {    //Para la autentificación
        return mAuth
    }

    fun mDatabase(): FirebaseDatabase {    //Para leer o escribir en la base de datos
        return database
    }

    fun getPlantBenefits():FirebaseFirestore{
        return BDD
    }
}
