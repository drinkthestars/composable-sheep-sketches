package trnt.sheepsketches.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
import com.canvas.sketch.SketchWithCache
import glm_.value
import nstv.design.theme.Grid
import nstv.design.theme.TextUnit
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
    var useGradientShader by remember { mutableStateOf(false) }
    val path = remember { Path() }

    val gradientShader = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gradientShader()
        } else {
            null
        }
    }
    val gradientShaderBrush = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && gradientShader != null) {
            ShaderBrush(gradientShader)
        } else {
            null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SketchWithCache(
            speed = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { time ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                gradientShader?.setFloatUniform(
                    "iResolution",
                    this.size.width, this.size.height
                )
                gradientShader?.setFloatUniform("iTime", time.value * 2f)
            }

            onDrawBehind {
                path.reset()

                drawLegs()

                if (usePerlin) {
                    if (gradientShaderBrush != null && useGradientShader) {
                        drawTrippyFluffPathWithPerlin(
                            path = path,
                            time = time,
                            noiseMax = noiseMax,
                            fluffBrush = gradientShaderBrush
                        )
                    } else {
                        drawTrippyFluffPathWithPerlin(
                            path = path,
                            time = time,
                            noiseMax = noiseMax
                        )
                    }
                } else {
                    if (gradientShaderBrush != null && useGradientShader) {
                        drawTrippyFluffPathSimplex(
                            time = time,
                            noiseMax = noiseMax,
                            fluffBrush = gradientShaderBrush
                        )
                    } else {
                        drawTrippyFluffPathSimplex(
                            time = time,
                            noiseMax = noiseMax
                        )
                    }
                }
                drawHead()
            }
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

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onErrorContainer,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                modifier = modifier
                    .fillMaxWidth(),
            ) {
                Text(
                    modifier = Modifier.padding(Grid.Two),
                    fontSize = TextUnit.Sixteen,
                    text = "Use Android 13 (API 33) for gradient shader option!"
                )
            }
        } else {
            CheckBoxLabel(
                modifier = Modifier.fillMaxWidth(),
                text = "Use Gradient Shader",
                checked = useGradientShader,
                onCheckedChange = { checked ->
                    useGradientShader = checked
                }
            )
        }
    }
}

@Preview
@Composable
private fun TrippyFluffPathPreview() {
    TrippyFluffPath()
}
