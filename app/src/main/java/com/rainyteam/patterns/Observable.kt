package com.rainyteam.patterns

interface Observable {
    val observers: ArrayList<Observer>
    fun add(observer: Observer) {
        observers.add(observer)
    }

    fun remove(observer: Observer) {
        observers.remove(observer)
    }

    fun dataChanged() {
        observers.forEach { it.update() }
    }
}