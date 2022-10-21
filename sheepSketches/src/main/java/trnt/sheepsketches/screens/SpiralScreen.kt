package trnt.sheepsketches.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
import nstv.canvasExtensions.guidelines.drawAxis
import nstv.canvasExtensions.guidelines.drawGrid
import nstv.canvasExtensions.nextItemLoop
import nstv.design.theme.Grid
import nstv.design.theme.components.CheckBoxLabel
import nstv.design.theme.components.LabeledText
import nstv.design.theme.components.SliderLabelValue
import trnt.sheepsketches.draw.SpiralType
import trnt.sheepsketches.draw.drawSpiral
import kotlin.math.roundToInt

const val maxNumberOfItems = 100

@Composable
fun SpiralScreen(modifier: Modifier = Modifier) {

    var numberOfItems by remember { mutableStateOf(50) }
    var spiralType by remember { mutableStateOf(SpiralType.Archimedean) }
    var useSheep by remember { mutableStateOf(true) }
    var noiseSheep by remember { mutableStateOf(false) }
    var trippySheep by remember { mutableStateOf(false) }
    var uniformPointDiameter by remember { mutableStateOf(false) }
    var spin by remember { mutableStateOf(false) }
    var counterclockwise by remember { mutableStateOf(false) }
    var noiseColor by remember { mutableStateOf(false) }
    var noisePointDiameter by remember { mutableStateOf(false) }

    var showGuidelines by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Spacer(modifier = modifier.padding(Grid.One))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            if (showGuidelines) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawGrid()
                    drawAxis()
                }
            }

            Sketch(
                modifier = Modifier.fillMaxSize(),
                speed = 1f,
            ) { time ->
                drawSpiral(
                    time = time,
                    numberOfPoints = numberOfItems,
                    spiralType = spiralType,
                    uniformPointDiameter = uniformPointDiameter,
                    counterclockwise = counterclockwise,
                    useSheep = useSheep,
                    noiseSheep = noiseSheep,
                    trippySheep = trippySheep,
                    showGuidelines = showGuidelines,
                    spin = spin,
                    noiseColor = noiseColor,
                    noisePointDiameter = noisePointDiameter,
                )
            }
        }

        LabeledText(
            modifier = Modifier.fillMaxWidth(),
            label = "Current Spiral Type: ",
            body = spiralType.name,
        )

        SliderLabelValue(
            text = "# Items",
            value = numberOfItems.toFloat(),
            valueRange = 10f..maxNumberOfItems.toFloat(),
            onValueChange = {
                numberOfItems = it.roundToInt()
            }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                spiralType =
                    SpiralType.values().nextItemLoop(SpiralType.valueOf(spiralType.name))
            }
        ) {
            val text = "Change Type"
            Text(text = text)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(Grid.One)
                )
        ) {

            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "SHEEP IT!",
                checked = useSheep,
                onCheckedChange = { useSheep = it }
            )
            AnimatedVisibility(visible = useSheep) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = Grid.Two)
                ) {
                    CheckBoxLabel(
                        text = "Noised Sheep",
                        checked = noiseSheep,
                        onCheckedChange = { noiseSheep = it }
                    )
                    CheckBoxLabel(
                        text = "Trippy Sheep",
                        checked = trippySheep,
                        onCheckedChange = { trippySheep = it }
                    )
                }
            }

            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Uniform Point Diameter",
                checked = uniformPointDiameter,
                onCheckedChange = { uniformPointDiameter = it }
            )

            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Spin",
                checked = spin,
                onCheckedChange = { spin = it }
            )
            AnimatedVisibility(visible = spin) {
                CheckBoxLabel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = Grid.Two),
                    text = "Counterclockwise",
                    checked = counterclockwise,
                    onCheckedChange = { counterclockwise = it }
                )
            }
            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Noised Color",
                checked = noiseColor,
                onCheckedChange = { noiseColor = it }
            )

            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Noised Point Diameter",
                checked = noisePointDiameter,
                onCheckedChange = { noisePointDiameter = it }
            )

            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Show Guidelines",
                checked = showGuidelines,
                onCheckedChange = { showGuidelines = it }
            )
        }
    }
}

@Preview
@Composable
private fun SpiralScreenPreview() {
    SpiralScreen()
}
