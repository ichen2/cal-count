package com.example.caloriecounter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var currentCalories = 200f
    val currentCaloriesData = MutableLiveData(currentCalories)
    val targetCaloriesData = MutableLiveData(2000f)

    fun addCalories(calories: Float) {
        currentCalories += calories
        currentCaloriesData.value = currentCalories
    }

    fun setGoal (calories: Float) {
        targetCaloriesData.value = calories
    }
}