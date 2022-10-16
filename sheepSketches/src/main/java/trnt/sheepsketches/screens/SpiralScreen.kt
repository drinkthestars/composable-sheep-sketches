package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
import nstv.canvasExtensions.guidelines.GuidelineAlpha
import nstv.canvasExtensions.guidelines.GuidelineStrokeWidth
import nstv.canvasExtensions.maths.getCurveControlPoint
import nstv.design.theme.Grid
import nstv.design.theme.components.CheckBoxLabel
import nstv.sheep.drawComposableSheep
import java.lang.Math.toDegrees
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun SpiralScreen(modifier: Modifier = Modifier) {

    var useSheep by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = modifier.padding(Grid.Two))
        Sketch(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            speed = 1f,
        ) { time ->
            drawSpiral(
                time = time,
                useSheep = useSheep,
            )
        }

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "Make it SHEEP!",
            checked = useSheep,
            onCheckedChange = { useSheep = it }
        )
    }
}

fun DrawScope.drawSpiral(
    time: Float,
    numberOfPoints: Int = 50,
    pointSeparation: Int = 70,
    useSheep: Boolean = false,
    showGuidelines: Boolean = false,
) {
    val path = Path().apply {
        moveTo(center.x, center.y)
    }

    var previousPoint = Offset(center.x, center.y)

    for (index in 0 until numberOfPoints) {
        val angle = toRadians((index * 137.5))
        val radius = pointSeparation * sqrt(index.toFloat())

        val timeShiftedAngle = angle + time

        val colorHue = toDegrees(timeShiftedAngle).toFloat().mod(360f)
        val x = radius * cos(timeShiftedAngle).toFloat() + center.x
        val y = radius * sin(timeShiftedAngle).toFloat() + center.y
        val pointRadius = 28f

        val newPoint = Offset(x, y)
        val controlPoint = getCurveControlPoint(previousPoint, newPoint, center)
        path.apply {
            quadraticBezierTo(
                x1 = controlPoint.x,
                y1 = controlPoint.y,
                x2 = x,
                y2 = y
            )
        }

        if (showGuidelines) {
            drawCircle(
                color = Color.Black.copy(alpha = GuidelineAlpha.low),
                radius = 2f,
                center = controlPoint
            )
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

@Preview
@Composable
private fun SpiralScreenPreview() {
    SpiralScreen()
}
