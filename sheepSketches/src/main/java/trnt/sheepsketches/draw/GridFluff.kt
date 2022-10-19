package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.canvas.sketch.TWO_PI
import com.canvas.sketch.lerp
import com.canvas.sketch.map
import glm_.Java
import glm_.glm
import glm_.vec4.Vec4
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawFlowFieldFluff(
    time: Float,
    circleRadius: Float,
    circleCenterOffset: Offset,
    dotCount: Int
) {
    (0 until dotCount).forEach { x ->
        (0 until dotCount).forEach { y ->
            // working between 0 and 1 aka U/V Space
            val u = x / (dotCount - 1).toFloat()
            val v = y / (dotCount - 1).toFloat()

            // lerp to get a value between 0 and 1
            val posX = lerp(
                min = circleCenterOffset.x - circleRadius,
                max = circleCenterOffset.x + circleRadius,
                norm = u
            )
            val posY = lerp(
                min = circleCenterOffset.y - circleRadius,
                max = circleCenterOffset.y + circleRadius,
                norm = v
            )

            val isInCircle = isInCircle(posX, posY, circleRadius)
            if (isInCircle) {
                // Lines 4D NOISE
                val r = 35f
                val startX = posX - r / 2f
                val startY = posY - r / 2f

                val noise = Java.glm.simplex(
                    Vec4(
                        x = u,
                        y = v,
                        z = 15f * cos(TWO_PI * time / 5f),
                        w = 15f * sin(TWO_PI * time / 5f)
                    )
                )
                val radians = noise * TWO_PI

                val endX = startX + (r * sin(radians))
                val endY = startY + (r * cos(radians))
                // Rainbow
                // val hue = (noise * 360f).absoluteValue
                val hue = map(noise, -1f, 1f, 170f, 300f)
                val color = Color.hsv(
                    hue = hue, saturation = 1f, value = 1f
                )
                drawLine(
                    start = Offset(startX, startY),
                    strokeWidth = 4f,
                    end = Offset(endX, endY),
                    color = color
                )
            }
        }
    }
}

fun DrawScope.drawCustomDotFluff(
    time: Float,
    circleRadius: Float,
    dotSizes: List<Float>,
    maxDotSize: Float,
    minDotSize: Float,
    circleCenterOffset: Offset,
    dotCount: Int
) {
    (0 until dotCount).forEach { x ->
        (0 until dotCount).forEach { y ->
            // working between 0 and 1 aka U/V Space
            val u = x / (dotCount - 1).toFloat()
            val v = y / (dotCount - 1).toFloat()

            // lerp to get a value between 0 and 1
            val posX = lerp(
                min = circleCenterOffset.x - circleRadius,
                max = circleCenterOffset.x + circleRadius,
                norm = u
            )
            val posY = lerp(
                min = circleCenterOffset.y - circleRadius,
                max = circleCenterOffset.y + circleRadius,
                norm = v
            )

            val isInCircle = isInCircle(posX, posY, circleRadius)
            if (isInCircle) {
                val maxDotSizeDelta = 10f
                val minDotSizeDelta = 6f
                val originalSize = dotSizes[dotCount * x + y]
                val newSize = originalSize + map(
                    sin((v * 2f + time * 40) * TWO_PI) + glm.cos((u * 2f + time * 20) * TWO_PI),
                    -2f, 2f,
                    minDotSizeDelta, maxDotSizeDelta
                )
                val shiftedX = posX + map(
                    sin((u + time * 12) * TWO_PI),
                    -1f, 1f,
                    -10f, 10f
                )
                val shiftedY = posY + map(
                    glm.cos((v * 2f + time * 15) * TWO_PI),
                    -1f, 1f,
                    -10f, 10f
                )

                val color = Color.hsv(
                    hue = hue(
                        size = newSize,
                        minDotSize = minDotSize + minDotSizeDelta,
                        maxDotSize = maxDotSize + maxDotSizeDelta
                    ),
                    saturation = 0.4f, value = 1f, alpha = 0.9f
                )
                drawCircle(
                    color = color,
                    radius = newSize,
                    center = Offset(shiftedX, shiftedY)
                )
            }
        }
    }
}

private fun hue(
    size: Float,
    minDotSize: Float,
    maxDotSize: Float,
    minHue: Float = 200f,
    maxHue: Float = 300f
) = map(
    value = size + 100,
    sourceMin = minDotSize + 100,
    sourceMax = maxDotSize + 100,
    destMin = maxHue,
    destMax = minHue
).coerceIn(minHue, maxHue)

fun DrawScope.drawGridFluff(
    time: Float,
    circleRadius: Float,
    circleCenterOffset: Offset,
    dotCount: Int
) {
    (0 until dotCount).forEach { x ->
        (0 until dotCount).forEach { y ->
            // working between 0 and 1 aka U/V Space
            val u = x / (dotCount - 1).toFloat()
            val v = y / (dotCount - 1).toFloat()

            // lerp to get a value between 0 and 1
            val posX = lerp(
                min = circleCenterOffset.x - circleRadius,
                max = circleCenterOffset.x + circleRadius,
                norm = u
            )
            val posY = lerp(
                min = circleCenterOffset.y - circleRadius,
                max = circleCenterOffset.y + circleRadius,
                norm = v
            )

            val isInCircle = isInCircle(posX, posY, circleRadius)
            if (isInCircle) {
                val maxDotRad = 17f
                val minDotRad = 5f
                val effectiveRad = map(
                    sin((v + time * 20) * TWO_PI) + glm.cos((u + time * 20) * TWO_PI),
                    -2f, 2f,
                    minDotRad, maxDotRad
                )
                val shiftedX = posX + map(
                    sin((u + time * 10) * TWO_PI),
                    -1f, 1f,
                    -10f, 10f
                )
                val shiftedY = posY + map(
                    glm.cos((v * 2f + time * 10) * TWO_PI),
                    -1f, 1f,
                    -10f, 10f
                )

                // Slightly different effect
//                val effectiveRad = (u + v) / 2f * maxDotRad
                val color = Color.hsv(
                    hue = map(
                        value = effectiveRad,
                        sourceMin = minDotRad,
                        sourceMax = maxDotRad,
                        destMin = 300f,
                        destMax = 190f
                    ),
                    saturation = 0.3f, value = 1f
                )
                drawCircle(
                    color = color,
                    radius = effectiveRad,
                    center = Offset(shiftedX, shiftedY)
                )
            }
        }
    }
}

private fun DrawScope.isInCircle(
    x: Float,
    y: Float,
    radius: Float
): Boolean {
    val distToCentSq = glm.pow(x - center.x, 2f) + glm.pow(y - center.y, 2f)
    return distToCentSq < glm.pow(radius, 2f)
}
