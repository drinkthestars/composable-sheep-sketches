package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.canvas.sketch.map
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import nstv.canvasExtensions.maths.FullCircleAngleInRadians
import nstv.design.theme.SheepColor
import nstv.sheep.getDefaultSheepRadius
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import kotlin.math.cos
import kotlin.math.sin

private const val AngleStep = 0.1f

fun DrawScope.drawTrippySheep(
    time: Float,
    noiseMax: Float = 2f,
    usePerlin: Boolean = true,
    circleRadius: Float = this.getDefaultSheepRadius(),
    circleCenterOffset: Offset = this.center,
    fluffBrush: Brush = SolidColor(SheepColor.Orange),
) {
    drawLegs(
        circleRadius = circleRadius,
        circleCenterOffset = circleCenterOffset,
    )

    if (usePerlin) {

        drawTrippyFluffPathWithPerlin(
            time = time,
            noiseMax = noiseMax,
            circleRadius = circleRadius,
            circleCenterOffset = circleCenterOffset,
            fluffBrush = fluffBrush,
        )
    } else {
        drawTrippyFluffPathSimplex(
            time = time,
            noiseMax = noiseMax,
            circleRadius = circleRadius,
            circleCenterOffset = circleCenterOffset,
            fluffBrush = fluffBrush,
        )
    }
    drawHead(
        circleRadius = circleRadius,
        circleCenterOffset = circleCenterOffset,
    )
}

fun DrawScope.drawTrippyFluffPathWithPerlin(
    time: Float,
    noiseMax: Float,
    path: Path = Path(),
    circleRadius: Float = this.getDefaultSheepRadius(),
    circleCenterOffset: Offset = this.center,
    fluffBrush: Brush = SolidColor(SheepColor.Orange),
) {

    val minRadius = circleRadius * 0.95f
    val maxRadius = circleRadius * 1.2f

    var angle = 0.0f
    while (angle < FullCircleAngleInRadians) {
        val timeShiftedRadius = angle + time * 0.005f

        val cosAngle = cos(angle)
        val sinAngle = sin(angle)

        val xOffset = map(
            value = cos(timeShiftedRadius + time * 10f),
            sourceMin = -1f,
            sourceMax = 1f,
            destMin = 0f,
            destMax = noiseMax
        )
        val yOffset = map(
            value = sin(timeShiftedRadius + time * 3f),
            sourceMin = -1f,
            sourceMax = 1f,
            destMin = 0f,
            destMax = noiseMax
        )

        // 3d perlin noise for closed path
        val perlinNoise = glm.perlin(Vec3(x = xOffset, y = yOffset, z = time * 0.7f))

        val r = map(perlinNoise, -1f, 1f, minRadius, maxRadius)
        val point = Offset(r * cosAngle, r * sinAngle) + circleCenterOffset
        when (angle) {
            0f -> path.moveTo(point.x, point.y)
            else -> path.lineTo(point.x, point.y)
        }

        angle += AngleStep
    }

    drawPath(path = path, brush = fluffBrush, style = Fill)
}

fun DrawScope.drawTrippyFluffPathSimplex(
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
        val sinAngle = glm.sin(angle).toFloat()

        val xOffset = map(cosAngle, -1f, 1f, 0f, noiseMax)
        val yOffset = map(sinAngle, -1f, 1f, 0f, noiseMax)
        val noise =
            glm.simplex(
                Vec2(
                    xOffset, yOffset + time
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
