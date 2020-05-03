package com.rainyteam.model

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.model.mutation.MutationBatch
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

    suspend fun getUserPlantsDead(user: String): MutableList<Plants>? {
        var resultPlants: MutableList<Plants>? = mutableListOf()
        var actualUserPlant: UserPlants
        try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereEqualTo("status", -1)
                .orderBy("plant_id")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        launch(coroutineContext) {
                            actualUserPlant = document.toObject(UserPlants::class.java)
                            var plant = async {
                                getPlant(actualUserPlant.plantId)!!
                            }.await()
                            plant.setStatus(actualUserPlant.status)
                            resultPlants!!.add(plant)
                        }
                        //this.getPlant()
                    }
                }.await() // job.cancel()
            return resultPlants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getUserPlantsWither(user: String): MutableList<Plants>? {
        var resultPlants: MutableList<Plants>? = mutableListOf()
        var actualUserPlant: UserPlants
        try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereEqualTo("status", 0)
                .orderBy("plant_id")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        launch(coroutineContext) {
                            actualUserPlant = document.toObject(UserPlants::class.java)
                            var plant = async {
                                getPlant(actualUserPlant.plantId)!!
                            }.await()
                            plant.setStatus(actualUserPlant.status)
                            resultPlants!!.add(plant)
                        }
                        //this.getPlant()
                    }
                }.await() // job.cancel()
            return resultPlants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getAllPlants(user: String): MutableList<Plants>? {
        var resultPlants: MutableList<UserPlants>? = mutableListOf()
        var userPlants: MutableList<String>? = mutableListOf()
        var boughtPlants: MutableList<Plants>? = mutableListOf()
        var buyPlants: MutableList<Plants>? = mutableListOf()
        try {
            launch(coroutineContext) {
               // userPlants = async { getUserPlantsId(user) }.await()
            }
            this.BDD.collection("Plants")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val actualPlant = document.toObject(Plants::class.java)!!
                        if (userPlants!!.contains(document.id)) {
                            boughtPlants!!.add(actualPlant)
                        } else {
                            buyPlants!!.add(actualPlant)
                        }
                    }
                }.await()
            return buyPlants?.let { boughtPlants!!.plus(it).toMutableList() }
        } catch (e: Exception) {
            Log.d("CONNECTION", e.message)
            return null
        }
    }

    suspend fun getUserPlantsId(user: String): MutableList<UserPlants>? {
        var resultPlants: MutableList<UserPlants>? = mutableListOf()
        var actualUserPlant: UserPlants
        try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        actualUserPlant = document.toObject(UserPlants::class.java)
                        resultPlants!!.add(actualUserPlant)
                    }
                }.await() // job.cancel()s
            return resultPlants
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }

    suspend fun getUserPlantsAlive(user: String): MutableList<Plants>? {
        var resultPlants: MutableList<Plants>? = mutableListOf()
        var actualUserPlant: UserPlants
        try {
            this.BDD.collection("User-Plants")
                .whereEqualTo("userId", user)
                .whereGreaterThan("status", 0)
                //.orderBy("plant_id")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        launch(coroutineContext) {
                            actualUserPlant = document.toObject(UserPlants::class.java)
                            var plant = async(coroutineContext) {
                                getPlant(actualUserPlant.plantId)!!
                            }.await()
                            plant.setStatus(actualUserPlant.status)
                            resultPlants!!.add(plant)
                        }
                        //this.getPlant()
                    }
                }.await()// job.cancel()
            return resultPlants
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

    suspend fun setUser(user: User){
        val dataUser = hashMapOf(
            "age" to user.getAge(),
            "exercise" to user.getExercise(),
            "hasInfo" to user.hasInfo,
            "height" to user.getHeight(),
            "maxWater" to user.getMaxWater(),
            "music" to user.music,
            "rainyCoins" to user.getRainyCoins(),
            "sex" to user.getSex(),
            "username" to user.getUsername(),
            "weight" to user.getWeight()
        )
        this.BDD.collection("Users")
            .document(user.getEmail())
            .set(dataUser)
    }
    suspend fun buyPlantToUser(user: User, plantToAdd: Plants) {
        var userPlant: UserPlants = UserPlants(plantToAdd.getName(), 100, user.getEmail())
        var documentName: String = user.getEmail() + "-" + plantToAdd.getName()
        val dataUser = hashMapOf(
            "age" to user.getAge(),
            "exercise" to user.getExercise(),
            "hasInfo" to user.hasInfo,
            "height" to user.getHeight(),
            "maxWater" to user.getMaxWater(),
            "music" to user.music,
            "rainyCoins" to user.getRainyCoins(),
            "sex" to user.getSex(),
            "username" to user.getUsername(),
            "weight" to user.getWeight()
        )
        this.BDD.collection("User-Plants")
            .document(documentName)
            .set(userPlant)
        this.BDD.collection("Users")
            .document(user.getEmail())
            .set(dataUser)
    }

}
