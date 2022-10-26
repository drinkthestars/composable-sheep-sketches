package trnt.sheepsketches.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import trnt.sheepsketches.draw.drawDynamicHue
import trnt.sheepsketches.draw.drawDynamicPositionFluff
import trnt.sheepsketches.draw.drawDynamicSizeFluff
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

    // Mode.MorphPositions
    var radius by remember { mutableStateOf(15f) }

    //  Mode.MorphPositionsAndSize + Mode.MorphPositionsSizeAndHue
    var minDotRad by remember { mutableStateOf(5f) }
    var maxDotRad by remember { mutableStateOf(17f) }
    var xAbsVariance by remember { mutableStateOf(10f) }
    var yAbsVariance by remember { mutableStateOf(10f) }
    var xFreq by remember { mutableStateOf(1f) }
    var yFreq by remember { mutableStateOf(2f) }
    var hueMax by remember { mutableStateOf(300f) }
    var hueMin by remember { mutableStateOf(190f) }
    var hueSat by remember { mutableStateOf(70f) }
    var hueValue by remember { mutableStateOf(100f) }

    Column(
        modifier = modifier
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
                sheep = sheep,
                radius = radius
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
                sheep = sheep,
                minDotRad = minDotRad,
                maxDotRad = maxDotRad,
                xAbsVariance = xAbsVariance,
                yAbsVariance = yAbsVariance,
                xFreq = xFreq,
                yFreq = yFreq
            )
            Mode.MorphPositionsSizeAndHue -> PositionsSizeHueFluff(
                headColor = headColor,
                legColor = legColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines,
                dotCount = dotCount,
                sheep = sheep,
                minDotRad = minDotRad,
                maxDotRad = maxDotRad,
                xAbsVariance = xAbsVariance,
                yAbsVariance = yAbsVariance,
                xFreq = xFreq,
                yFreq = yFreq,
                hueMax = hueMax,
                hueMin = hueMin,
                hueSat = hueSat,
                hueValue = hueValue
            )
        }
        SliderLabelValue(
            text = "# Dots",
            value = dotCount.toFloat(),
            valueRange = 10f..50f,
            onValueChange = {
                dotCount = it.roundToInt()
            }
        )
        when (mode) {
            Mode.MorphPositions -> {
                Column(
                    modifier = Modifier
                        .padding(Grid.One)
                        .fillMaxWidth()
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(1.dp)
                            .padding(vertical = Grid.One)
                            .background(MaterialTheme.colorScheme.outline)
                    )
                    SliderLabelValue(
                        text = "radius",
                        value = radius,
                        valueRange = 5f..20f,
                        onValueChange = { radius = it }
                    )
                }
            }
            Mode.MorphPositionsAndSize, Mode.MorphPositionsSizeAndHue -> {
                Column(
                    modifier = Modifier
                        .padding(vertical = Grid.One)
                        .fillMaxWidth()
                        .wrapContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = (Modifier.size(Grid.One)))
                    SliderLabelValue(
                        text = "minDotRad",
                        value = minDotRad,
                        valueRange = 1f..10f,
                        onValueChange = { minDotRad = it }
                    )
                    SliderLabelValue(
                        text = "maxDotRad",
                        value = maxDotRad,
                        valueRange = 12f..25f,
                        onValueChange = { maxDotRad = it }
                    )
                    SliderLabelValue(
                        text = "xAbsVariance",
                        value = xAbsVariance,
                        valueRange = 5f..15f,
                        onValueChange = { xAbsVariance = it }
                    )
                    SliderLabelValue(
                        text = "yAbsVariance",
                        value = yAbsVariance,
                        valueRange = 5f..15f,
                        onValueChange = { yAbsVariance = it }
                    )
                    SliderLabelValue(
                        text = "xFreq",
                        value = xFreq,
                        valueRange = 1f..5f,
                        onValueChange = { xFreq = it }
                    )
                    SliderLabelValue(
                        text = "yFreq",
                        value = yFreq,
                        valueRange = 1f..5f,
                        onValueChange = { yFreq = it }
                    )
                    if (mode == Mode.MorphPositionsSizeAndHue) {
                        SliderLabelValue(
                            text = "hueMax",
                            value = hueMax,
                            valueRange = 195f..300f,
                            onValueChange = { hueMax = it }
                        )
                        SliderLabelValue(
                            text = "hueMin",
                            value = hueMin,
                            valueRange = 50f..194f,
                            onValueChange = { hueMin = it }
                        )
                        SliderLabelValue(
                            text = "hueSat",
                            value = hueSat,
                            valueRange = 0f..100f,
                            onValueChange = { hueSat = it }
                        )
                        SliderLabelValue(
                            text = "hueValue",
                            value = hueValue,
                            valueRange = 0f..100f,
                            onValueChange = { hueValue = it }
                        )
                    }
                }
            }
            Mode.Static -> Unit
        }

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
    minDotRad: Float,
    maxDotRad: Float,
    xAbsVariance: Float,
    yAbsVariance: Float,
    xFreq: Float,
    yFreq: Float,
    hueMax: Float,
    hueMin: Float,
    hueSat: Float,
    hueValue: Float
) {
    Sketch(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) { time ->
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
            dotCount = dotCount,
            minDotRad = minDotRad,
            maxDotRad = maxDotRad,
            xAbsVariance = xAbsVariance,
            yAbsVariance = yAbsVariance,
            xFreq = xFreq,
            yFreq = yFreq,
            hueMax = hueMax,
            hueMin = hueMin,
            hueSat = hueSat,
            hueValue = hueValue
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
    minDotRad: Float,
    maxDotRad: Float,
    xAbsVariance: Float,
    yAbsVariance: Float,
    xFreq: Float,
    yFreq: Float,
) {
    Sketch(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) { time ->
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
            dotCount = dotCount,
            minDotRad = minDotRad,
            maxDotRad = maxDotRad,
            xAbsVariance = xAbsVariance,
            yAbsVariance = yAbsVariance,
            xFreq = xFreq,
            yFreq = yFreq
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
    Sketch(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) { time ->
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
    radius: Float
) {
    Sketch(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f), showControls = true
    ) {
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
            dotCount = dotCount,
            radius = radius
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
