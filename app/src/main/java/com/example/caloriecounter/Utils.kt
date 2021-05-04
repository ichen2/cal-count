package com.example.caloriecounter

fun Float.toDegrees(targetCalories: Float) : Float {
    return this * 360 / targetCalories
}