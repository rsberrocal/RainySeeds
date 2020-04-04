package com.rainyteam.model

class Plants(private var name: String,
             private var scientificName: String,
             private var benefits: String,
             private var uses: String,
             private var precautions: String,
             private var moneyGenerated: Int,
             private val priceMultiplier: Int,
             private var status: Int) {
    //Getters
    fun getName() :String {
        return this.name
    }
    fun getScientificName(): String{
        return this.scientificName
    }
    fun getBenefits(): String{
        return this.benefits
    }
    fun getUses(): String{
        return this.uses
    }
    fun getPrecautions(): String{
        return this.precautions
    }
    fun getMoneyGenerated(): Int{
        return this.moneyGenerated
    }
    fun getMoneyCost(): Int{
        return this.moneyGenerated * this.priceMultiplier
    }
    fun getStatus(): Int{
        return this.status
    }

}