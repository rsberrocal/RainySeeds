package com.rainyteam.model

import kotlin.String as String
import kotlin.collections.ArrayList as ArrayList

//Default constructor
class User {
    private var username: String = ""
    private var email: String = ""
    private var password: String = ""
    private var age: Int = 0
    private var weight: Int = 0
    private var height: Float = 0.0f
    private var sex: String = ""
    private var exercise: Int = 0
    private var maxWater: Float = 0.0f
    var rainycoins: Int
    var plantList: ArrayList<String>

    constructor() {

    }

    init {
        this.rainycoins = 0
        this.plantList = ArrayList<String>()
    }


    //Getters
    fun getEmail(): String {
        return this.email
    }

    fun getUsername(): String {
        return this.username
    }

    fun getPassword(): String {
        return this.password
    }

    fun getWeight(): Int {
        return this.weight
    }

    fun getAge(): Int {
        return this.age
    }

    fun getHeight(): Float {
        return this.height
    }

    fun getSex(): String {
        return this.sex
    }

    fun getExercise(): Int {
        return this.exercise
    }

    fun getGreenhousePlants(): ArrayList<String>? {
        return null;
    }

    fun getEncyclopediaPlants(): ArrayList<String>? {
        return null;
    }

    fun getMaxWater(): Float{
        return maxWater
    }

}

