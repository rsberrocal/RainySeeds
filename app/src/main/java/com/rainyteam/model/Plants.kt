package com.rainyteam.model

class Plants {
    private var name: String? = null
    private var scientificName: String? = null
    private var benefits: String? = null
    private var uses: String? = null
    private var precautions: String? = null
    private var money: Int? = null
    private val moneyMultiplier: Int? = 1
    private var status: Int = -2
    private var imagePlant: String? = null

    constructor() {

    }

    constructor(
        name: String, scientificName: String, benefits: String, uses: String,
        precautions: String, money: Int, status: Int
    ) {
        this.name = name
        this.scientificName = scientificName
        this.benefits = benefits
        this.uses = uses
        this.precautions = precautions
        this.money = money
        this.status = status
    }

    //Getters
    fun getName(): String {
        return this.name!!
    }

    fun getScientificName(): String {
        return this.scientificName!!
    }

    fun getBenefits(): String? {
        return this.benefits
    }

    fun getUses(): String? {
        return this.uses
    }

    fun getPrecautions(): String? {
        return this.precautions
    }

    fun getMoney(): Int {
        return this.money!!
    }

    fun getMoneyGenerated(): Int {
        return this.money!! * this.moneyMultiplier!!
    }

    fun getStatus(): Int? {
        return this.status
    }

    fun getImagePlant(): String? {
        return imagePlant
    }

    //provisional
    fun getPotImagePlant(): String? {
        return "pot_" + scientificName!!.toLowerCase().replace(" ", "_")
    }

    fun setImageName(name: String) {
        this.imagePlant = name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun setStatus(status: Int) {
        this.status = status;
    }

    fun isAlive(): Boolean {
        return this.status > 0
    }

    fun isWither(): Boolean{
        return this.status == 0
    }

    fun isDead(): Boolean{
        return this.status == -1
    }

    fun isBought(): Boolean{
        return this.status == -2
    }

}