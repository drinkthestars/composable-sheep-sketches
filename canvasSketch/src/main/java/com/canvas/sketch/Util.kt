package com.canvas.sketch

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import glm_.glm
import glm_.glm.PIf
import glm_.vec2.Vec2
import kotlin.math.abs

val TWO_PI = 2f * PIf
val HALF_PI = PIf / 2f

fun gaussianRandomNoise(
    x: Float,
    y: Float,
    xMean: Float = 6f,
    xDeviation: Float = 0.5f,
    yMean: Float = 10f,
    yDeviation: Float = 0.2f
) = abs(
    glm.simplex(
        Vec2(
            x = x + glm.gaussRand(xMean, xDeviation),
            y = y + glm.gaussRand(yMean, yDeviation)
        )
    )
)

@Composable
fun screenSize(): Pair<Float, Float> {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidthPx = with(density) { config.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { config.screenHeightDp.dp.toPx() }
    return Pair(screenWidthPx, screenHeightPx)
}

fun norm(value: Float, min: Float, max: Float): Float {
    return (value - min) / (max - min)
}

fun lerp(norm: Float, min: Float, max: Float): Float {
    return (max - min) * norm + min
}

fun map(
    value: Float,
    sourceMin: Float,
    sourceMax: Float,
    destMin: Float,
    destMax: Float
): Float {
    return lerp(
        norm = norm(
            value = value,
            min = sourceMin,
            max = sourceMax
        ),
        min = destMin,
        max = destMax
    )
}

fun Int.map(sourceMin: Float, sourceMax: Float, destMin: Float, destMax: Float): Float {
    return lerp(norm(this.toFloat(), sourceMin, sourceMax), destMin, destMax)
}

/** Matrix Multiplication */
fun Array<FloatArray>.multiply(
    array: FloatArray,
    firstRows: Int,
    firstColumn: Int,
    secondColumn: Int
): FloatArray {
    val product = FloatArray(firstRows) { 0f }
    for (i in 0 until firstRows) {
        repeat((0 until secondColumn).count()) {
            for (k in 0 until firstColumn) {
                product[i] += this[i][k] * array[k]
            }
        }
    }
    return product
}
