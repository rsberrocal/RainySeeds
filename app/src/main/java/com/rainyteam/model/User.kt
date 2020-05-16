package com.rainyteam.model

import kotlin.String as String
import kotlin.collections.ArrayList as ArrayList

//Default constructor
class User {
    private var username: String = ""
    private var email: String = ""
    private var age: Long = 0
    private var weight: Int = 0
    private var height: Float = 0.0f
    private var sex: String = ""
    private var exercise: Int = 0
    private var maxWater: Float = 0.0f
    private var rainycoins: Int
    var music: Boolean = true
    var hasInfo: Boolean = false

    constructor() {

    }

    init {
        this.rainycoins = 0
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

    fun getAge(): Long {
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
    fun setName(name: String){
        this.username = name
    }
    fun setHeight(height: Float){
        this.height = height
    }
    fun setWeight(weight: Int){
        this.weight = weight
    }
    fun setSex(sex: String){
        this.sex = sex
    }
    fun setExercise(exercise: Int){
        this.exercise = exercise
    }
    fun setHasInf(x: Boolean){
        this.hasInfo = x
    }
    fun setAge(age: Long){
        this.age = age
    }
    fun setMaxWater() {
        var temp: Float
        temp = this.weight * 0.035f
        if (this.sex == "Male") {
            temp *= 1.20f;
        }
        temp *= (this.exercise / 10) * 1.2f + 1;
        this.maxWater = temp*1000;
    }

    fun getMaxWater(): Float {
        return maxWater
    }


}

