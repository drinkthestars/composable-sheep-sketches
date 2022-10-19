package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.canvas.sketch.Sketch
import nstv.design.theme.ComposableSheepTheme
import nstv.design.theme.Grid
import nstv.design.theme.components.RadioGroup
import nstv.design.theme.components.SliderLabelValue
import nstv.sheep.model.FluffStyle
import nstv.sheep.model.Sheep
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import trnt.sheepsketches.draw.drawDynamicPositionFluff
import trnt.sheepsketches.draw.drawDynamicSizeFluff
import trnt.sheepsketches.draw.drawDynamicHue
import trnt.sheepsketches.draw.drawStaticGridFluff
import kotlin.math.roundToInt

// Prob use slider for this 👀
private const val DefaultDotCount = 25

@Composable
fun GridFluffScreen(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }
    Box(modifier = modifier.fillMaxSize()) {
        GridFluffSheep(
            modifier = Modifier.fillMaxSize(),
            sheep = sheep
        )
    }
}

@Composable
fun GridFluffSheep(
    modifier: Modifier,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    var dotCount by remember { mutableStateOf(DefaultDotCount) }
    var mode by remember { mutableStateOf(Mode.MorphPositionsAndSize) }

    Column(modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.padding(Grid.One))
        when (mode) {
            Mode.Static -> StaticFluff(
                headColor = headColor,
                legColor = legColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines,
                dotCount = dotCount,
                sheep = sheep
            )
            Mode.MorphPositions -> PositionsFluff(
                headColor = headColor,
                legColor = legColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines,
                dotCount = dotCount,
                sheep = sheep
            )
            Mode.MorphPositionsAndSize -> PositionsAndSizeFluff(
                headColor = headColor,
                legColor = legColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines,
                dotCount = dotCount,
                sheep = sheep
            )
            Mode.MorphPositionsSizeAndHue -> PositionsSizeHueFluff(
                headColor = headColor,
                legColor = legColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines,
                dotCount = dotCount,
                sheep = sheep
            )
        }
        SliderLabelValue(
            text = "# Items",
            value = dotCount.toFloat(),
            valueRange = 10f..50f,
            onValueChange = {
                dotCount = it.roundToInt()
            }
        )
        Spacer(modifier = Modifier.padding(Grid.One))
        RadioGroup(
            options = Mode.values().toList(),
            onSelected = { mode = it },
            displayValue = { it.name }
        )
        Spacer(modifier = Modifier.padding(Grid.One))
    }
}

@Composable
private fun PositionsSizeHueFluff(
    modifier: Modifier = Modifier,
    dotCount: Int,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    Sketch(modifier = modifier.fillMaxWidth()
        .aspectRatio(1f)) { time ->
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)

        drawLegs(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            legs = sheep.legs,
            legColor = legColor,
            showGuidelines = showGuidelines
        )

        drawDynamicHue(
            time = time,
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            dotCount = dotCount
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

@Composable
private fun PositionsAndSizeFluff(
    modifier: Modifier = Modifier,
    dotCount: Int,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    Sketch(modifier = modifier.fillMaxWidth()
        .aspectRatio(1f)) { time ->
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)

        drawLegs(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            legs = sheep.legs,
            legColor = legColor,
            showGuidelines = showGuidelines
        )

        drawDynamicSizeFluff(
            time = time,
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            dotCount = dotCount
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

@Composable
private fun PositionsFluff(
    modifier: Modifier = Modifier,
    dotCount: Int,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    Sketch(modifier = modifier.fillMaxWidth()
        .aspectRatio(1f)) { time ->
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)

        drawLegs(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            legs = sheep.legs,
            legColor = legColor,
            showGuidelines = showGuidelines
        )

        drawDynamicPositionFluff(
            time = time,
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            dotCount = dotCount
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

@Composable
private fun StaticFluff(
    modifier: Modifier = Modifier,
    dotCount: Int,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    Sketch(modifier = modifier.fillMaxWidth()
        .aspectRatio(1f), showControls = true) {
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)

        drawLegs(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            legs = sheep.legs,
            legColor = legColor,
            showGuidelines = showGuidelines
        )

        drawStaticGridFluff(
            circleCenterOffset = circleCenterOffset,
            circleRadius = circleRadius,
            dotCount = dotCount
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


private enum class Mode(val value: String) {
    Static("Static"),
    MorphPositions("Morph Positions"),
    MorphPositionsAndSize("Morph Positions + Size"),
    MorphPositionsSizeAndHue("Morph Positions + Size + Hue")
}

@Preview(showBackground = true, widthDp = 320, backgroundColor = 0xFF292C34)
@Composable
private fun PreviewSheepGuidelines() {
    ComposableSheepTheme {
        GridFluffSheep(
            modifier = Modifier.size(300.dp),
            sheep = Sheep(FluffStyle.Random()),
            showGuidelines = false
        )
    }
}
