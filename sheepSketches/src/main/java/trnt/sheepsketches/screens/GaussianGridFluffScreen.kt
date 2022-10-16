package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.canvas.sketch.Sketch
import glm_.glm
import nstv.design.theme.ComposableSheepTheme
import nstv.sheep.model.FluffStyle
import nstv.sheep.model.Sheep
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import trnt.sheepsketches.draw.drawCustomDotFluff

// Prob use slider for this 👀
private val DotCount = 25

@Composable
fun GaussianGridFluff(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }
    Box(modifier = modifier.fillMaxSize()) {
        GaussianGridFluffSheep(
            modifier = Modifier.fillMaxSize(),
            sheep = sheep,
            fluffBrush = SolidColor(sheep.fluffColor)
        )
    }
}

@Composable
fun GaussianGridFluffSheep(
    modifier: Modifier,
    sheep: Sheep,
    fluffBrush: Brush,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    val mean = 15f
    val stdDev = 5f
    val dotSizes = remember {
        List(DotCount * DotCount) {
            glm.gaussRand(mean, stdDev)
        }
    }
    Sketch(modifier = modifier) { time ->
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)
        drawLegs(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            legs = sheep.legs,
            legColor = legColor,
            showGuidelines = showGuidelines
        )

        // min/max = 2 std deviations from mean for gaussian distro
        drawCustomDotFluff(
            time = time,
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            dotCount = DotCount,
            dotSizes = dotSizes,
            minDotSize = mean - 2 * stdDev,
            maxDotSize = mean + 2 * stdDev,
        )

        drawHead(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            headAngle = sheep.headAngle,
            headColor = headColor,
            eyeColor = eyeColor,
            glassesColor = glassesColor,
            glassesTranslation = glassesTranslation,
            showGuidelines = showGuidelines
        )
    }
}

@Preview(showBackground = true, widthDp = 320, backgroundColor = 0xFF292C34)
@Composable
private fun PreviewGaussianGridFluffSheep() {
    ComposableSheepTheme {
        GaussianGridFluffSheep(
            modifier = Modifier.size(300.dp),
            sheep = Sheep(FluffStyle.Random()),
            fluffBrush = SolidColor(Color.LightGray),
            showGuidelines = false
        )
    }
}
