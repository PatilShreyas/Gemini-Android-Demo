package dev.shreyaspatil.gemini.demo.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    on: Dp,
    off: Dp,
    shape: androidx.compose.ui.graphics.Shape,
) = drawBehind {
    val stroke = Stroke(
        width.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            floatArrayOf(on.toPx(), off.toPx()),
            0f,
        ),
    )
    drawOutline(
        outline = shape.createOutline(
            size,
            layoutDirection = layoutDirection,
            this
        ), color = color, style = stroke
    )
}
