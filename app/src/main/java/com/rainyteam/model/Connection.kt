package com.rainyteam.model

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception

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

    fun getPlantBenefits(): FirebaseFirestore {
        return BDD
    }

    suspend fun getUser(user: String?): User? {
        var actualUser: User? = null
        return try {
            val data = this.BDD.collection("Users")
                .document(user!!)
                .get()
                .addOnSuccessListener { document ->
                    actualUser = document.toObject(User::class.java)
                }.await()
            return actualUser
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getPlant(plant: String): Plants? {
        var actualPlant: Plants? = null
        return try {
            val data = this.BDD.collection("Plants")
                .document(plant)
                .get()
                .addOnSuccessListener { document ->
                    actualPlant = document.toObject(Plants::class.java)
                }.await()
            return actualPlant
        } catch (e: Exception) {
            return null
        }
    }

    suspend fun getHistory(user: String): History? {
        var actualHistory: History? = null
        return try {
            this.BDD.collection("History")
                .document(user)
                .get()
                .addOnSuccessListener { document ->
                    actualHistory = document.toObject(History::class.java)
                }.await()
            return actualHistory
        } catch (e: Exception) {
            return null
        }
    }
}
