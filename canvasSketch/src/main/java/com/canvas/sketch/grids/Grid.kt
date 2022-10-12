package com.canvas.sketch.grids

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.canvas.sketch.Sketch
import com.canvas.sketch.TWO_PI
import com.canvas.sketch.captureAndShare
import glm_.Java
import glm_.glm.linearRand
import glm_.glm.simplex
import glm_.value
import glm_.vec2.Vec2
import glm_.vec4.Vec4
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

private const val GridXCount = 100

@Composable
fun Grid() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        var random by remember { mutableStateOf(0f) }
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(100.dp))
        Row {
            IconButton(onClick = {
                random = linearRand(0f, 1f)
            }) {
                Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
            }
            IconButton(onClick = {
                captureAndShare(
                    width = size.width,
                    height = size.height,
                    rect = boundsInWindow.toAndroidRect(),
                    context = context
                )
            }) {
                Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
            }
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            GridSketch(
                random,
                modifier = Modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    }
            )
        }
    }
}

@Composable
private fun GridSketch(random: Float, modifier: Modifier = Modifier) {
    var offsetY by remember { mutableStateOf(GridXCount.toFloat()) }
    val size by remember { mutableStateOf(Size.Zero) }

    Sketch(
//        speed = 0f,
        modifier = modifier
            .fillMaxSize(0.8f)
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val originalY = offsetY.value
                    val summedY = originalY + dragAmount.y
                    val newValueY = summedY.coerceIn(0f, size.height)
                    offsetY = newValueY
                }
            }
    ) { time ->
        for (x in 0 until this.size.width.toInt() step GridXCount) {
            for (y in 0 until this.size.height.toInt() step GridXCount) {
                val noise = simplex(
                    Vec2(x.toFloat() + time, y.toFloat())
                ).absoluteValue
                val hue = noise * 200f + random * 160f
                val size = GridXCount.toFloat()
//                val size = map(sin(time * TWO_PI), -1f, 1f, GridXCount.toFloat()/3f, GridXCount.toFloat())
                val fdnoise = Java.glm.simplex(
                    Vec4(
                        x, y, 10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
                    )
                ) * 30f
                val centeredX = x.toFloat() - size / 2f
                val centeredY = y.toFloat() - size / 2f
//                val z = map(
//                    value = sin(time * 3f + (y * 0.01f)),
//                    sourceMin = -1f,
//                    sourceMax = 1f,
//                    destMin = (this.size.width / 2 * (1 - noise)) + 1000f, //
//                    destMax = this.size.width
//                )

//                val zx = map(centeredX / z, 0f, 1f, 0f, this.size.width) + fdnoise
//                val zy = map(centeredY / z, 0f, 1f, 0f, this.size.height) - fdnoise

//                if (random > noise) {
                drawRect(
                    topLeft = Offset(centeredX, centeredY),
                    size = Size(size * 0.75f, size * 0.75f),
                    color = Color.DarkGray,
                    style = Stroke(3f)
                )
//                }

                // blocks or grids
//                drawRect(
//                    topLeft = Offset(x.toFloat(), y.toFloat()),
//                    size = Size(GridXCount.toFloat(), GridXCount.toFloat()),
//                    color = Color.hsv(noise * 200f + random * 160f, 1f, 1f),
//                    style = if (hue < 250) Stroke(1f) else Fill
//                )

                // blocks, hue and saturation
//                drawRect(
//                    topLeft = Offset(x.toFloat(), y.toFloat()),
//                    size = Size(GridXCount.toFloat(), GridXCount.toFloat()),
//                    color = Color.hsv(noise * 200f + random * 160f, noise, 1f),
//                    style = Fill
//                )
            }
        }
    }
}

private fun DrawScope.drawRect(
    x: Int,
    y: Int,
    dotCount: Float,
    pixel: Color
) {
    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}
