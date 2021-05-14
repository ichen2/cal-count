package com.example.caloriecounter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.caloriecounter.ui.theme.CalorieCounterTheme
import java.lang.String.format
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : ComponentActivity() {

    private val model: MainViewModel by viewModels()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getPreferences(Context.MODE_PRIVATE)
        model.setCalories(preferences.getFloat("currentCalories", 0f))
        model.setGoal(preferences.getFloat("targetCalories", 2000f))
        val previousDate = dateFormat.parse(preferences.getString("previousDate", dateFormat.format(Calendar.getInstance().time)))
        //if(previousDate.time - Calendar.getInstance().time.time >= TimeUnit.DAYS.toMillis(1))
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
            putString("previousData", dateFormat.format(Calendar.getInstance().time))
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