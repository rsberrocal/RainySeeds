package com.rainyteam.model

import kotlin.String as String
import kotlin.collections.ArrayList as ArrayList

//Default constructor
class User {
    private var username: String = ""
    private var email: String = ""
    private var age: Int = 0
    private var weight: Int = 0
    private var height: Float = 0.0f
    private var sex: String = ""
    private var exercise: Int = 0
    private var maxWater: Float = 0.0f
    private var rainycoins: Int
    var music: Boolean = true
    var hasInfo: Boolean = false
    var plantList: ArrayList<String>

    constructor() {

    }

    init {
        this.rainycoins = 0
        this.plantList = ArrayList<String>()
        this.setMaxWater()
    }


    //Getters
    fun getEmail(): String {
        return this.email
    }

    fun getUsername(): String {
        return this.username
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

    fun getRainyCoins(): Int {
        return this.rainycoins;
    }

    fun setRainyCoins(coins: Int) {
        this.rainycoins = coins;
    }

    fun setEmail(email: String) {
        this.email = email
    }

    fun getGreenhousePlants(): ArrayList<String>? {
        return null;
    }

    fun getEncyclopediaPlants(): ArrayList<String>? {
        return null;
    }

    fun setMaxWater() {
        var temp: Float
        temp = this.weight * 0.035f
        if (this.sex == "Male") {
            temp *= 1.20f;
        }
        temp *= (this.exercise / 10) * 1.2f + 1;
        this.maxWater = temp;
    }

    fun getMaxWater(): Float {
        return maxWater
    }


}

