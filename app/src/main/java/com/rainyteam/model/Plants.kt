package com.rainyteam.model

class Plants {
    private var name: String? = null
    private var scientificName: String? = null
    private var benefits: String? = null
    private var uses: String? = null
    private var precautions: String? = null
    private var moneyGenerated: Int? = null
    private val priceMultiplier: Int? = null
    private var status: Int? = null
    private var image: String? = null

    constructor(){

    }

    constructor(name: String){

    }

    //Getters
    fun getName(): String? {
        return this.name
    }

    fun getScientificName(): String? {
        return this.scientificName
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

    fun getMoneyGenerated(): Int? {
        return this.moneyGenerated
    }

    fun getMoneyCost(): Int {
        return this.moneyGenerated!! * this.priceMultiplier!!
    }

    fun getStatus(): Int? {
        return this.status
    }

    fun getImage(): String? {
        return this.image
    }

}