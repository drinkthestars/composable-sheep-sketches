package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
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
    uniformPointDiameter: Boolean = true,
    counterclockwise: Boolean = false,
    useSheep: Boolean = false,
    showGuidelines: Boolean = false,
) {
    val path = Path().apply { moveTo(center.x, center.y) }

    var previousPoint = Offset(center.x, center.y)
    for (index in 0 until numberOfPoints) {

        val (angle, radius) = getAngleAndRadius(
            time = time,
            index = index,
            spiralType = spiralType,
            counterclockwise = counterclockwise,
        )

        val colorHue = angle.toDegrees().mod(360f)
        val x = radius * cos(angle) + center.x
        val y = radius * sin(angle) + center.y
        val pointRadius = if (uniformPointDiameter) 28f else radius.div(10f)
        val newPoint = Offset(x, y)

        if (showGuidelines) {
//            val controlPoint = getCurveControlPoint(previousPoint, newPoint, center)
//            path.apply {
//                quadraticBezierTo(
//                    x1 = controlPoint.x,
//                    y1 = controlPoint.y,
//                    x2 = x,
//                    y2 = y
//                )
//            }
//            drawCircle(
//                color = Color.Black.copy(alpha = GuidelineAlpha.low),
//                radius = 2f,
//                center = controlPoint
//            )
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
        previousPoint = newPoint
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
    time: Float,
    index: Int,
    spiralType: SpiralType,
    counterclockwise: Boolean = false,
): Pair<Float, Float> {
    val adjustedTime = 1.5f * if (counterclockwise) -time else time

    return when (spiralType) {
        Phyllotaxis -> {
            val pointSeparation = 70f
            val angle = (index * 137.5f).toRadians()
            val radius = pointSeparation * sqrt(index.toFloat())
            val timeShiftedAngle = angle + adjustedTime

            timeShiftedAngle to radius
        }
        Archimedean -> {
            val pointSeparation = 20f
            val angle = (index.toFloat() * pointSeparation).toRadians()
            val radius = pointSeparation * angle

            val timeShiftedAngle = angle + adjustedTime
            timeShiftedAngle to radius
        }
        Logarithmic -> {
            val pointSeparation = 30f
            val angle = (index.toFloat() * pointSeparation).toRadians()
            // val angle = (index.toFloat() * 15f).toRadians() * 1.5f
            val radius = (pointSeparation / 2f) * exp(angle / (pointSeparation / 4f))

            val timeShiftedAngle = angle + adjustedTime
            timeShiftedAngle to radius
        }
    }
}
