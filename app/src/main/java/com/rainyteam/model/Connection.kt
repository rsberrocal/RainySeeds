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

    fun mBDD(): FirebaseFirestore {
        return BDD
    }

    suspend fun getUser(user: String): User? {
        var actualUser: User? = null
        return try {
            this.BDD.collection("Users")
                .document(user)
                .get()
                .addOnSuccessListener { document ->
                    actualUser = document.toObject(User::class.java)
                    actualUser!!.setEmail(document.id)
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

    suspend fun getAllPlants(startPlant: Plants?): MutableList<Plants>? {
        var plants: MutableList<Plants>? = mutableListOf()
        var actualPlant: Plants? = null
        return try {
            var query = this.BDD.collection("Plants")
            if (startPlant != null) {
                query.startAfter(startPlant)
            }
            query.limit(12)
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

    suspend fun getUserPlantsAlive(user: String): MutableList<UserPlants>? {
        var plants: MutableList<UserPlants>? = mutableListOf()
        var actualUserPlant: UserPlants? = null
        return try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        actualUserPlant = document.toObject(UserPlants::class.java)
                        plants!!.add(actualUserPlant!!);
                    }
                }.await()
            // job.cancel()
            return plants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getCountUserPlantsAlive(user: String): Int {
        var count = 0
        return try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThanOrEqualTo("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        count++
                    }
                }.await()
            // job.cancel()
            return count
        } catch (e: Exception) {
            println(e.message)
            return 0
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

    suspend fun getCountDeadPlants(user: String): Int {
        var count = 0
        var actualPlant: Plants? = null
        return try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereLessThan("status", 0)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        count++
                    }
                }
                .await()
            return count
        } catch (e: Exception) {
            return 0

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

    suspend fun buyPlantToUser(user: User, plantToAdd: Plants) {
        user.setEmail("rainyseeds@gmail.com")
        var userPlant: UserPlants = UserPlants(plantToAdd.getName(), 100, user.getEmail())
        var documentName: String = user.getEmail() + "-" + plantToAdd.getName()
        this.BDD.collection("User-Plants")
            .document(documentName)
            .set(userPlant)
        this.BDD.collection("Users")
            .document(user.getEmail())
            .set(user)
    }

}
