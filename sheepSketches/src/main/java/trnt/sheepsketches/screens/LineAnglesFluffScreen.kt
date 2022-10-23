package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.canvas.sketch.Sketch
import nstv.design.theme.ComposableSheepTheme
import nstv.design.theme.Grid
import nstv.design.theme.components.SliderLabelValue
import nstv.sheep.model.FluffStyle
import nstv.sheep.model.Sheep
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import trnt.sheepsketches.draw.drawFlowFieldFluff
import trnt.sheepsketches.draw.drawLineAnglesFluff
import kotlin.math.roundToInt

// Prob use slider for this 👀
private const val DefaultDotCount = 30

@Composable
fun LineAnglesFluffScreen(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }
    LineAnglesFluffSheep(
        modifier = modifier,
        sheep = sheep,
    )
}

@Composable
fun LineAnglesFluffSheep(
    modifier: Modifier,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    // 0d to 360f
    var angle by remember { mutableStateOf(45f) }
    var dotCount by remember { mutableStateOf(DefaultDotCount) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Sketch(modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
            showControls = true
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

            drawLineAnglesFluff(
                angleDegrees = angle,
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
        Spacer(modifier = Modifier.padding(Grid.Three))
        SliderLabelValue(
            text = "Line Angle",
            value = angle,
            valueRange = 0f..360f,
            onValueChange = { angle = it }
        )
        Spacer(modifier = Modifier.padding(Grid.Two))
        SliderLabelValue(
            text = "# Dots",
            value = dotCount.toFloat(),
            valueRange = 10f..50f,
            onValueChange = { dotCount = it.roundToInt() }
        )
    }
}

@Preview(showBackground = true, widthDp = 320, backgroundColor = 0xFF292C34)
@Composable
private fun FlowFieldFluffSheepPreview() {
    ComposableSheepTheme {
        LineAnglesFluffSheep(
            modifier = Modifier.size(300.dp),
            sheep = Sheep(FluffStyle.Random()),
            showGuidelines = false
        )
    }
}
