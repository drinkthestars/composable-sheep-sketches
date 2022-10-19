package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.canvas.sketch.map
import glm_.glm
import glm_.vec3.Vec3
import nstv.canvasExtensions.guidelines.GuidelineAlpha
import nstv.canvasExtensions.guidelines.GuidelineStrokeWidth
import nstv.canvasExtensions.maths.toDegrees
import nstv.canvasExtensions.maths.toRadians
import nstv.sheep.drawComposableSheep
import trnt.sheepsketches.draw.SpiralType.Archimedean
import trnt.sheepsketches.draw.SpiralType.Logarithmic
import trnt.sheepsketches.draw.SpiralType.Phyllotaxis
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt

enum class SpiralType {
    Phyllotaxis,
    Archimedean,
    Logarithmic,
}

fun DrawScope.drawSpiral(
    time: Float,
    numberOfPoints: Int = 50,
    spiralType: SpiralType = Archimedean,
    noiseMax: Float = 25f,
    uniformPointDiameter: Boolean = true,
    spin: Boolean = false,
    noiseColor: Boolean = false,
    noisePointDiameter: Boolean = false,
    counterclockwise: Boolean = false,
    useSheep: Boolean = false,
    showGuidelines: Boolean = false,

) {
    val path = Path().apply { moveTo(center.x, center.y) }

    for (index in 0 until numberOfPoints) {

        val (angle, radius) = getAngleAndRadius(
            index = index,
            spiralType = spiralType,
        )

        val adjustedTime = 1.5f * if (counterclockwise) -time else time
        val timeShiftedAngle = angle + if (spin) adjustedTime else 0f
        val x = radius * cos(timeShiftedAngle) + center.x
        val y = radius * sin(timeShiftedAngle) + center.y

        val perlinNoise = glm.perlin(Vec3(x = angle, y = radius, z = time * 1.5f))
        val angleDegrees = angle.toDegrees().mod(360f)
        val angleNoise = if (noiseColor) {
            map(
                value = perlinNoise,
                sourceMin = -1f,
                sourceMax = 1f,
                destMin = angleDegrees - noiseMax * 0.2f,
                destMax = angleDegrees + noiseMax * 0.2f
            )
        } else angleDegrees

        val colorHue = angleNoise.toDegrees().mod(360f)
        val basePointRadius = if (uniformPointDiameter) 28f else radius.div(10f)
        val pointRadius = if (noisePointDiameter) {
            map(
                value = perlinNoise,
                sourceMin = -1f,
                sourceMax = 1f,
                destMin = basePointRadius - noiseMax,
                destMax = basePointRadius + noiseMax
            )
        } else basePointRadius

        val newPoint = Offset(x, y)

        if (showGuidelines) {
            path.apply { lineTo(newPoint.x, newPoint.y) }
        }

        if (useSheep) {
            drawComposableSheep(
                fluffColor = Color.hsv(
                    hue = colorHue,
                    saturation = 1f,
                    value = 1f
                ),
                circleRadius = pointRadius,
                circleCenterOffset = newPoint,
            )
        } else {
            drawCircle(
                color = Color.hsv(
                    hue = colorHue,
                    saturation = 1f,
                    value = 1f
                ),
                radius = pointRadius,
                center = newPoint
            )
        }
    }

    if (showGuidelines) {
        drawPath(
            path = path,
            color = Color.Black.copy(alpha = GuidelineAlpha.low),
            style = Stroke(
                GuidelineStrokeWidth
            )
        )
    }
}

private fun getAngleAndRadius(
    index: Int,
    spiralType: SpiralType,
): Pair<Float, Float> {
    return when (spiralType) {
        Phyllotaxis -> {
            val pointSeparation = 70f
            val angle = (index * 137.5f).toRadians()
            val radius = pointSeparation * sqrt(index.toFloat())

            angle to radius
        }
        Archimedean -> {
            val pointSeparation = 20f
            val angle = (index.toFloat() * pointSeparation).toRadians()
            val radius = pointSeparation * angle

            angle to radius
        }
        Logarithmic -> {
            val pointSeparation = 30f
            val angle = (index.toFloat() * pointSeparation).toRadians()
            // val angle = (index.toFloat() * 15f).toRadians() * 1.5f
            val radius = (pointSeparation / 2f) * exp(angle / (pointSeparation / 4f))

            angle to radius
        }
    }
}
