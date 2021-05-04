package com.example.caloriecounter

import android.graphics.Typeface
import android.view.Gravity
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

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
    }) { state ->
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
        OutlinedText("${currentCalories.roundToInt()}/${targetCalories.roundToInt()}")
    }
}

@Composable
fun OutlinedText(text: String) {
    val textPaintStroke = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        textSize = MaterialTheme.typography.h1.fontSize.value
        color = MaterialTheme.colors.onBackground.toArgb()
        strokeWidth = 30f
        strokeJoin = android.graphics.Paint.Join.ROUND
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    val textPaint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.FILL
        textSize = MaterialTheme.typography.h1.fontSize.value
        color = MaterialTheme.colors.background.toArgb()
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
        Text(text = description, modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.caption)
    }
}