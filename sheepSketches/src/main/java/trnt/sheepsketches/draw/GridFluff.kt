package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.canvas.sketch.TWO_PI
import com.canvas.sketch.lerp
import com.canvas.sketch.map
import glm_.glm
import kotlin.math.sin

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
                val maxDotSizeDelta = 8f
                val minDotSizeDelta = 5f
                val originalSize = dotSizes[dotCount * x + y]
                val newSize = originalSize + map(
                    sin((v + time * 40) * TWO_PI) + glm.cos((u + time * 20) * TWO_PI),
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
                    saturation = 0.3f, value = 0.5f
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
    value = size,
    sourceMin = minDotSize,
    sourceMax = maxDotSize,
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
