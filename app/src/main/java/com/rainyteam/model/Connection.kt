package com.rainyteam.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

class Connection : CoroutineScope {
    val mAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val BDD = FirebaseFirestore.getInstance()

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    /*override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }*/

    //Singleton, creem una nova instància si no hi ha cap creada anteriorment.

    fun mAuth(): FirebaseAuth {
        return mAuth
    }

    fun mDatabase(): FirebaseDatabase {
        return database
    }

    suspend fun getUser(user: String?): User? {
        var actualUser: User? = null
        return try {
            this.BDD.collection("Users")
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
            this.BDD.collection("Plants")
                .document(plant)
                .get()
                .addOnSuccessListener { document ->
                    actualPlant = document.toObject(Plants::class.java)
                    actualPlant!!.setName(document.id)
                    actualPlant!!.setImageName(
                        "plant_" + actualPlant!!.getScientificName().toLowerCase().replace(
                            " ",
                            "_"
                        )
                    )
                }.await()
            return actualPlant
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getAllPlants(): MutableList<Plants>? {
        var plants: MutableList<Plants>? = mutableListOf()
        var actualPlant: Plants? = null
        return try {
            this.BDD.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        actualPlant = document.toObject(Plants::class.java)
                        actualPlant!!.setName(document.id)
                        actualPlant!!.setImageName(
                            "plant_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                " ",
                                "_"
                            )
                        )
                        plants!!.add(actualPlant!!)
                    }
                }.await()
            return plants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getUserPlantsAlive(user: String, isGreenhouse: Boolean): MutableList<Plants>? {
        var plants: MutableList<Plants>? = mutableListOf()
        var actualPlant: Plants? = null
        var actualUserPlant: UserPlants? = null
        return try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThan("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        actualUserPlant = document.toObject(UserPlants::class.java)
                        this.BDD.collection("Plants")
                            .document(actualUserPlant!!.plantId)
                            .get()
                            .addOnSuccessListener { document ->
                                actualPlant = document.toObject(Plants::class.java)
                                actualPlant!!.setName(document.id)
                                var plantName = ""
                                if (isGreenhouse) {
                                    plantName =
                                        "pot_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                            " ",
                                            "_"
                                        )
                                } else {
                                    plantName =
                                        "plant_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                            " ",
                                            "_"
                                        )
                                }
                                actualPlant!!.setImageName(plantName)
                                plants!!.add(actualPlant!!)
                            }
                    }
                }
                .await()
            delay(1000)
            // job.cancel()
            return plants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getDeadPlants(user: String): MutableList<Plants>? {
        var plants: MutableList<Plants>? = mutableListOf()
        var actualPlant: Plants? = null
        return try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereLessThan("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        actualPlant = document.toObject(Plants::class.java)
                        actualPlant!!.setName(document.id)
                        actualPlant!!.setImageName(
                            "dead_" + actualPlant!!.getScientificName().toLowerCase().replace(
                                " ",
                                "_"
                            )
                        )
                        plants!!.add(actualPlant!!)
                    }
                }
                .await()
            return plants
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

    suspend fun addHistory(user: String, historyToAdd: History) {
        this.BDD.collection("History")
            .document(user)
            .set(historyToAdd)
            .await()
    }

}
