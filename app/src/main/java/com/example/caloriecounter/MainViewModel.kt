package com.example.caloriecounter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var currentCalories = 0f
    val currentCaloriesData = MutableLiveData(currentCalories)
    val targetCaloriesData = MutableLiveData(2000f)

    fun addCalories(calories: Float) {
        currentCalories += calories
        currentCaloriesData.value = currentCalories
    }

    fun setCalories(calories: Float) {
        currentCalories += calories
        currentCaloriesData.value = currentCalories
    }

    fun getCalories() : Float {
        return currentCalories
    }

    fun setGoal (calories: Float) {
        targetCaloriesData.value = calories
    }

    fun getGoal(): Float {
        return targetCaloriesData.value ?: 0f
    }
}