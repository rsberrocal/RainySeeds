package com.rainyteam.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rainyteam.controller.R

class Plant {
    var iconsChar: Int? = 0
    var alphaChar: String? = null

    constructor(iconsChar: Int?, alphaChar: String?){
        this.iconsChar = iconsChar
        this.alphaChar = alphaChar
    }
}