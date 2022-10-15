package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.canvas.sketch.map
import glm_.glm.simplex
import glm_.glm.sin
import glm_.vec2.Vec2
import nstv.canvasExtensions.maths.FullCircleAngleInRadians
import nstv.design.theme.SheepColor
import nstv.sheep.getDefaultSheepRadius
import kotlin.math.cos

private const val AngleStep = 0.1f

fun DrawScope.drawTrippyFluffPath(
    time: Float,
    noiseMax: Float,
    circleRadius: Float = this.getDefaultSheepRadius(),
    circleCenterOffset: Offset = this.center,
    fluffBrush: Brush = SolidColor(SheepColor.Orange),
) {

    val pointList = mutableListOf<Offset>()
    val minRadius = circleRadius * 0.95f
    val maxRadius = circleRadius * 1.2f

    var angle = 0.0
    while (angle < FullCircleAngleInRadians) {
        val cosAngle = cos(angle).toFloat()
        val sinAngle = sin(angle).toFloat()

        val xOffset = map(cosAngle, -1f, 1f, 0f, noiseMax)
        val yOffset = map(sinAngle, -1f, 1f, 0f, noiseMax)
        val noise =
            simplex(
                Vec2(
                    xOffset, yOffset + time * 100f
                )
            )
        val r = map(noise, -1f, 1f, minRadius, maxRadius)
        pointList.add(Offset(r * cosAngle, r * sinAngle) + circleCenterOffset)
        angle += AngleStep
    }
    pointList.add(pointList.first())

    val path = Path().apply {
        pointList.firstOrNull()?.let {
            moveTo(it.x, it.y)
            pointList.forEach { offset ->
                lineTo(offset.x, offset.y)
            }
        }
    }

    drawPath(path = path, brush = fluffBrush, style = Fill)
}
