package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
import nstv.design.theme.components.CheckBoxLabel
import nstv.design.theme.components.SliderLabelValue
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import trnt.sheepsketches.draw.drawTrippyFluffPathSimplex
import trnt.sheepsketches.draw.drawTrippyFluffPathWithPerlin

@Composable
fun TrippyFluffPath(modifier: Modifier = Modifier) {

    var noiseMax by remember { mutableStateOf(2f) }
    var usePerlin by remember { mutableStateOf(true) }
    val path = remember { Path() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Sketch(
            speed = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { time ->
            path.reset()

            drawLegs()

            if (usePerlin) {

                drawTrippyFluffPathWithPerlin(
                    path = path,
                    time = time,
                    noiseMax = noiseMax
                )
            } else {
                drawTrippyFluffPathSimplex(
                    time = time,
                    noiseMax = noiseMax
                )
            }
            drawHead()
        }

        SliderLabelValue(
            text = "Max Noise",
            value = noiseMax,
            valueRange = 0f..10f,
            onValueChange = {
                noiseMax = it
            }
        )

        CheckBoxLabel(
            modifier = Modifier.fillMaxWidth(),
            text = "Use Perlin",
            checked = usePerlin,
            onCheckedChange = { checked ->
                usePerlin = checked
            }
        )
    }
}

@Preview
@Composable
private fun TrippyFluffPathPreview() {
    TrippyFluffPath()
}
