package com.example.caloriecounter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import android.preference.PreferenceManager

import android.content.SharedPreferences




class MainActivity : ComponentActivity() {

    private val model: MainViewModel by viewModels( )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getPreferences(Context.MODE_PRIVATE)
        model.setCalories(preferences.getFloat("currentCalories", 0f))
        model.setGoal(preferences.getFloat("targetCalories", 2000f))
        setContent {
            CalorieCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(model)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        saveData()
    }

    fun saveData() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putFloat("currentCalories", model.getCalories())
            putFloat("targetCalories", model.getGoal())
            apply()
        }
    }
}

@Composable
fun MainScreen(model: MainViewModel) {
    var dialogState by remember { mutableStateOf(DialogState.NONE) }
    val currentCalories by model.currentCaloriesData.observeAsState(0f)
    val targetCalories by model.targetCaloriesData.observeAsState(0f)
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
            InputDialog(
                onClose = { dialogState = DialogState.NONE },
                onConfirm =
                when(dialogState) {
                    DialogState.ADD_FOOD -> model::addCalories
                    DialogState.SET_GOAL -> model::setGoal
                    else -> { _ ->  }
                },
                dialogState = dialogState
            )
        }
    }
}