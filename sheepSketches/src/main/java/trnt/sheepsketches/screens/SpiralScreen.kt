package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
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
    var spiralType by remember { mutableStateOf(SpiralType.Phyllotaxis) }
    var useSheep by remember { mutableStateOf(false) }
    var uniformPointDiameter by remember { mutableStateOf(true) }
    var counterclockwise by remember { mutableStateOf(false) }
    var showGuidelines by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = modifier.padding(Grid.One))
        Sketch(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            speed = 1f,
        ) { time ->
            drawSpiral(
                time = time,
                numberOfPoints = numberOfItems,
                spiralType = spiralType,
                uniformPointDiameter = uniformPointDiameter,
                counterclockwise = counterclockwise,
                useSheep = useSheep,
                showGuidelines = showGuidelines,
            )
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

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "SHEEP IT!",
            checked = useSheep,
            onCheckedChange = { useSheep = it }
        )

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "Uniform Point Diameter",
            checked = uniformPointDiameter,
            onCheckedChange = { uniformPointDiameter = it }
        )

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "Counterclockwise",
            checked = counterclockwise,
            onCheckedChange = { counterclockwise = it }
        )

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "Show Guidelines",
            checked = showGuidelines,
            onCheckedChange = { showGuidelines = it }
        )
    }
}

@Preview
@Composable
private fun SpiralScreenPreview() {
    SpiralScreen()
}
