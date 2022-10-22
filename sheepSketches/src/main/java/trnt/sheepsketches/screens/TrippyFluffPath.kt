package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
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
import nstv.design.theme.components.CheckBoxLabel
import nstv.design.theme.components.LabeledText
import nstv.design.theme.components.SliderLabelValue
import trnt.sheepsketches.draw.NoiseType
import trnt.sheepsketches.draw.drawTrippySheep

@Composable
fun TrippyFluffPath(modifier: Modifier = Modifier) {

    var noiseMax by remember { mutableStateOf(2f) }
    var noiseType by remember { mutableStateOf(NoiseType.Perlin) }
    var showGuidelines by remember { mutableStateOf(false) }
    var static by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Sketch(
            speed = if (static) 0f else 3f,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { time ->
            drawTrippySheep(
                time = time,
                noiseMax = noiseMax,
                noiseType = noiseType,
                showGuidelines = showGuidelines,
            )
        }

        LabeledText(
            modifier = Modifier.fillMaxWidth(),
            label = "Current Noise Type: ",
            body = noiseType.name,
        )

        SliderLabelValue(
            text = "Max Noise",
            value = noiseMax,
            valueRange = 0f..10f,
            onValueChange = {
                noiseMax = it
            }
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                noiseType =
                    NoiseType.values().nextItemLoop(NoiseType.valueOf(noiseType.name))
            }
        ) {
            val text = "Change Type"
            Text(text = text)
        }

        CheckBoxLabel(
            text = "Show Guidelines",
            checked = showGuidelines,
            onCheckedChange = {
                showGuidelines = it
            }
        )

        CheckBoxLabel(
            text = "Static",
            checked = static,
            onCheckedChange = {
                static = it
            }
        )
    }
}

@Preview
@Composable
private fun TrippyFluffPathPreview() {
    TrippyFluffPath()
}
