package com.rainyteam.model

class UserPlants {
    var plantId: String = ""
    var status: Int = 0
    var userId: String = ""

    constructor() {

    }

    constructor(pId: String, status: Int, uId: String) {
        this.plantId = pId
        this.status = status
        this.userId = uId

    }

}