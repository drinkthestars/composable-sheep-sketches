package trnt.sheepsketches.draw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import com.canvas.sketch.map
import glm_.glm
import glm_.noise
import glm_.vec2.Vec2
import nstv.canvasExtensions.guidelines.GuidelineAlpha
import nstv.canvasExtensions.maths.FullCircleAngleInRadians
import nstv.design.theme.SheepColor
import nstv.sheep.getDefaultSheepRadius
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import kotlin.math.cos
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.random.Random

private const val AngleStep = 0.1f

enum class NoiseType {
    Perlin,
    Simplex,
    Random,
}

fun DrawScope.drawTrippySheep(
    time: Float,
    noiseMax: Float = 2f,
    noiseType: NoiseType = NoiseType.Perlin,
    circleRadius: Float = this.getDefaultSheepRadius(),
    circleCenterOffset: Offset = this.center,
    fluffBrush: Brush = SolidColor(SheepColor.Orange),
    showGuidelines: Boolean = false,
) {
    drawLegs(
        circleRadius = circleRadius,
        circleCenterOffset = circleCenterOffset,
    )

    drawTrippyFluff(
        time = time,
        noiseMax = noiseMax,
        noiseType = noiseType,
        circleRadius = circleRadius,
        circleCenterOffset = circleCenterOffset,
        fluffBrush = fluffBrush,
        showGuidelines = showGuidelines,
    )

    drawHead(
        circleRadius = circleRadius,
        circleCenterOffset = circleCenterOffset,
    )
}

fun DrawScope.drawTrippyFluff(
    time: Float,
    noiseMax: Float,
    noiseType: NoiseType,
    circleRadius: Float = this.getDefaultSheepRadius(),
    circleCenterOffset: Offset = this.center,
    fluffBrush: Brush = SolidColor(SheepColor.Orange),
    showGuidelines: Boolean = false,
) {
    println("--------- noiseType: $noiseType ------ ")
    val path = Path()
    val minRadius = circleRadius * 0.95f
    val maxRadius = circleRadius * 1.2f

    val points = mutableListOf<Offset>()

    var angle = 0.0f
    while (angle < FullCircleAngleInRadians) {

        val xOffset = map(
            value = cos(angle),
            sourceMin = -1f,
            sourceMax = 1f,
            destMin = 0f,
            destMax = noiseMax
        )

        val yOffset = map(
            value = sin(angle),
            sourceMin = -1f,
            sourceMax = 1f,
            destMin = 0f,
            destMax = noiseMax
        )

        val noise = when (noiseType) {
            NoiseType.Perlin -> glm.perlin(Vec2(x = xOffset, y = yOffset + time))
            NoiseType.Simplex -> glm.simplex(Vec2(x = xOffset, y = yOffset + time))
            NoiseType.Random -> Random(
                (xOffset + yOffset + time).times(100).roundToLong()
            ).nextLong(-100, 100).div(100f)
        }

        println("noise for angle $angle: $noise")

        val r = map(noise, -1f, 1f, minRadius, maxRadius)
        val point = Offset(r * cos(angle), r * sin(angle)) + circleCenterOffset
        points.add(point)

        when (angle) {
            0f -> path.moveTo(point.x, point.y)
            else -> path.lineTo(point.x, point.y)
        }

        angle += AngleStep
    }

    drawPath(path = path, brush = fluffBrush, style = Fill)

    if (showGuidelines) {
        drawCircle(
            color = SheepColor.Black.copy(alpha = GuidelineAlpha.normal),
            radius = minRadius,
            style = Stroke(4f),
            center = circleCenterOffset,
        )
        drawCircle(
            color = SheepColor.Black.copy(alpha = GuidelineAlpha.normal),
            radius = maxRadius,
            center = circleCenterOffset,
            style = Stroke(4f),
        )
        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            color = Color.Blue.copy(alpha = GuidelineAlpha.normal),
            strokeWidth = 16f,
            cap = StrokeCap.Round,
        )
    }
}
