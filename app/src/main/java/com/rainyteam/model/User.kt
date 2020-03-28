package com.rainyteam.model

import com.rainyteam.views.Plant
import kotlin.String as String
import kotlin.collections.ArrayList as ArrayList

//Default constructor
class User(
    private var nombre: String,
    private var email: String,
    private var password: String,
    private var age: Int,
    private var weight: Int,
    private var height: Float,
    private var sex: String,
    private var exercise: Int
) {
    var cartera: Int
    var plantList: ArrayList<String>

    init {
        this.cartera = 0
        this.plantList = ArrayList<String>()
    }
    //Getters
    fun getEmail() :String {
        return this.email
    }
    fun getName() :String {
        return this.nombre
    }
    fun getPassword() :String {
        return this.password
    }
    fun getWeight() :Int {
        return this.weight
    }
    fun getAge() :Int {
        return this.age
    }
    fun getHeight() :Float {
        return this.height
    }
    fun getSex() :String {
        return this.sex
    }
    fun getExercise() :Int {
        return this.exercise
    }
}

