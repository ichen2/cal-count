package com.example.caloriecounter

import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

enum class DialogState {
    NONE,
    ADD_FOOD,
    SET_GOAL
}

// stateful component - acts as a container for internal state
@Composable
fun RadialFilledDonut(currentCalories: Float = 0f, targetCalories: Float = 2000f) {
    var animated by remember { mutableStateOf(false) }
    val transition = updateTransition(animated)
    val degrees by transition.animateFloat(transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    }, label = "") { state ->
        if (state) currentCalories.toDegrees(targetCalories) else 0f
    }
    RadialFilledDonut(degrees, currentCalories, targetCalories)
    animated = true
}

// stateless component - gets values from stateful component and renders UI
@Composable
fun RadialFilledDonut(degrees: Float, currentCalories: Float, targetCalories: Float) {

    val RadialFillShape = GenericShape { size, _ ->
        arcTo(Rect(Offset(0f, 0f), size), -90f, degrees, false)
        lineTo(size.width / 2f, size.height / 2f)
        close()
    }
    Box(
        Modifier
            .size(300.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colors.onBackground),
        Alignment.Center
    ) {
        Box(
            Modifier
                .size(300.dp)
                .clip(RadialFillShape)
                .background(MaterialTheme.colors.primary)
        )
        Box(
            Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.background)
        )
        OutlinedText(
            text = "${currentCalories.roundToInt()}/${targetCalories.roundToInt()}",
            primaryColor = MaterialTheme.colors.background,
            outlineColor = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun OutlinedText(text: String, primaryColor: Color, outlineColor: Color) {
    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = MaterialTheme.typography.h1.fontSize.value
        color = outlineColor.toArgb()
        strokeWidth = 20f
        strokeJoin = android.graphics.Paint.Join.ROUND
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        textSize = MaterialTheme.typography.h1.fontSize.value
        color = primaryColor.toArgb()
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    val verticalOffset = 30f
    Canvas(
        modifier = Modifier
    )
    {
        drawIntoCanvas {

            it.nativeCanvas.drawText(
                text,
                0f,
                verticalOffset,
                textPaintStroke
            )
            it.nativeCanvas.drawText(
                text,
                0f,
                verticalOffset,
                textPaint
            )
        }
    }
}

@Composable
fun CircleButton(imageResource: Int? = null, onClick: () -> Unit = {}, description: String = "") {
    Box {
        Image(
            if (imageResource != null) painterResource(imageResource) else ColorPainter(
                MaterialTheme.colors.secondary
            ),
            description,
            Modifier
                .size(100.dp)
                .clip(
                    CircleShape
                )
                .clickable(onClick = onClick)
        )
        Text(
            text = description,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.caption
        )
    }
}

@Composable
fun InputDialog(close: () -> Unit, dialogState: DialogState) {
    var textfieldValue by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { close() },
        title = {
            Text(text =
                when(dialogState) {
                    DialogState.ADD_FOOD -> "Add Food"
                    DialogState.SET_GOAL -> "Set Goal"
                    DialogState.NONE -> "Error"
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                when(dialogState) {
                    DialogState.ADD_FOOD -> addCalories(textfieldValue.toFloat())
                    DialogState.SET_GOAL -> setGoal(textfieldValue.toFloat())
                    DialogState.NONE -> Log.e("inputDialog", "No dialog state specified")
                }
                close()
            }) {
                Text("Confirm")
            }
        },
        text = {
            Column {
                Text("Input calories")
                TextField(
                    value = textfieldValue,
                    onValueChange = { textfieldValue = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    )
}