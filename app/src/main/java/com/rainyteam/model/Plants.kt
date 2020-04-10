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
    private var imagePlant: String? = null

    constructor() {

    }

    constructor(
        name: String, scientificName: String, benefits: String, uses: String,
        precautions: String, moneyGenerated: Int, status: Int
    ) {
        this.name = name
        this.scientificName = scientificName
        this.benefits = benefits
        this.uses = uses
        this.precautions = precautions
        this.moneyGenerated = moneyGenerated
        this.status = status
        this.imagePlant = "plant_" + scientificName.toLowerCase().replace(" ", "_")
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

    fun getImagePlant(): String? {
        return this.imagePlant
    }

    fun setName(name: String) {
        this.name = name
    }

}