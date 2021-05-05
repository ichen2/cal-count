package com.example.caloriecounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.caloriecounter.ui.theme.CalorieCounterTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType

var currentCalories: Float by mutableStateOf(220f)
var targetCalories: Float by mutableStateOf(2000f)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalorieCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    var dialogState by remember { mutableStateOf(DialogState.NONE) }
    Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
        Spacer(Modifier.weight(1f))
        RadialFilledDonut(currentCalories, targetCalories)
        Spacer(Modifier.weight(3f))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            CircleButton(onClick = { dialogState = DialogState.ADD_FOOD }, description = "Add food")
            Spacer(Modifier.weight(1f))
            CircleButton(onClick = { dialogState = DialogState.SET_GOAL }, description = "Set goal")
        }
        if (dialogState != DialogState.NONE) {
            InputDialog(close = { dialogState = DialogState.NONE }, dialogState = dialogState)
        }
    }
}

fun addCalories(calories: Float) {
    currentCalories += calories
}

fun setGoal (calories: Float) {
    targetCalories = calories
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    CalorieCounterTheme {
        MainScreen()
    }
}