package com.rarilabs.rarime.ui.theme

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.scale
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Credits: https://www.reddit.com/r/androiddev/comments/tdva2j/comment/i0mmsqt/
fun Modifier.dropShadow(
    color: Color = Color.Black,
    alpha: Float = 0.08f,
    borderRadius: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 1.dp,
    blurRadius: Dp = 1.dp,
    spread: Dp = 0.dp,
) =
    this.drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            frameworkPaint.color = color.copy(alpha = 0.0f).toArgb()
            frameworkPaint.setShadowLayer(
                blurRadius.toPx(),
                offsetX.toPx(),
                offsetY.toPx(),
                color.copy(alpha = alpha).toArgb()
            )
            it.save()

            if (spread.value > 0) {
                fun calcSpreadScale(spread: Float, childSize: Float): Float {
                    return 1f + ((spread / childSize) * 2f)
                }

                it.scale(
                    calcSpreadScale(spread.toPx(), this.size.width),
                    calcSpreadScale(spread.toPx(), this.size.height),
                    this.center.x,
                    this.center.y
                )
            }

            it.drawRoundRect(
                0f,
                0f,
                this.size.width,
                this.size.height,
                borderRadius.toPx(),
                borderRadius.toPx(),
                paint
            )
            it.restore()
        }
    }