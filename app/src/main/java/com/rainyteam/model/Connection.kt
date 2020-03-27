package com.rainyteam.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Connection {
    private var database: DatabaseReference

    //Singleton, creem una nova instància si no hi ha cap creada anteriorment.
    init {
        database = FirebaseDatabase.getInstance().reference
    }

    fun main(args: Array<String>) {

    }

    fun mAuth(): FirebaseAuth {    //Para la autentificación
        return FirebaseAuth.getInstance()
    }

    fun mDatabase(): DatabaseReference {    //Para leer o escribir en la base de datos
        return database
    }

}
