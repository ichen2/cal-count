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
import androidx.compose.foundation.shape.RoundedCornerShape
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

enum class FilledComponentType {
    BAR,
    DONUT
}

// stateful component - acts as a container for internal state
@Composable
fun FilledComponent(type: FilledComponentType, current: Float = 0f, target: Float = 2000f) {
    var animated by remember { mutableStateOf(false) }
    val transition = updateTransition(animated)
    val percentage by transition.animateFloat(transitionSpec = {
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    }, label = "") { state ->
        if (state) Math.min(current / target, 1f) else 0f
    }
    when(type) {
        FilledComponentType.DONUT -> RadialFilledDonut(percentage, current, target)
        FilledComponentType.BAR -> FilledProgressBar(percentage, current, target)
    }
    animated = true
}

// stateless component - gets values from stateful component and renders UI
@Composable
fun RadialFilledDonut(percentage: Float, current: Float, target: Float) {

    val RadialFillShape = GenericShape { size, _ ->
        addArc(Rect(Offset(0f, 0f), size), -90f, 360f * percentage)
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
            text = "${current.roundToInt()}/${target.roundToInt()}",
            primaryColor = MaterialTheme.colors.background,
            outlineColor = MaterialTheme.colors.onBackground
        )
    }
}

@Composable
fun FilledProgressBar(percentage: Float, current: Float, target: Float) {
    Box(contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .size(200.dp, 20.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colors.onBackground),
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                Modifier
                    .size(200.dp * percentage, 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colors.primary)
            )
        }
        Text("$current / $target", color = MaterialTheme.colors.background)
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
fun InputDialog(onClose: () -> Unit, onConfirm: (Float) -> Unit, dialogState: DialogState) {
    if (dialogState == DialogState.NONE) {
        Log.e("inputDialog", "No dialog state specified")
        return
    }
    var textfieldValue by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = { onClose() },
        title = {
            Text(
                text =
                when (dialogState) {
                    DialogState.ADD_FOOD -> "Add Food"
                    DialogState.SET_GOAL -> "Set Goal"
                    else -> ""
                }
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(textfieldValue.toFloat())
                onClose()
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