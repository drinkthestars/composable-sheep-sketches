package trnt.sheepsketches.screens

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.SketchWithCache
import glm_.value
import nstv.canvasExtensions.nextItemLoop
import nstv.design.theme.Grid
import nstv.design.theme.TextUnit
import nstv.design.theme.components.CheckBoxLabel
import nstv.design.theme.components.LabeledText
import nstv.design.theme.components.SliderLabelValue
import trnt.sheepsketches.draw.NoiseType
import trnt.sheepsketches.draw.drawTrippySheep

enum class ShaderType {
    Gradient,
    NoodleZoom,
}

@Composable
fun TrippyFluffPath(modifier: Modifier = Modifier) {

    var noiseMax by remember { mutableStateOf(2f) }
    var useShader by remember { mutableStateOf(false) }
    val path = remember { Path() }
    var noiseType by remember { mutableStateOf(NoiseType.Perlin) }
    var shaderType by remember { mutableStateOf(ShaderType.Gradient) }
    var showGuidelines by remember { mutableStateOf(false) }
    var static by remember { mutableStateOf(false) }

    val shader by remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                when (shaderType) {
                    ShaderType.Gradient -> gradientShader()
                    ShaderType.NoodleZoom -> noodleZoomShader()
                }
            } else {
                null
            }
        }
    }
    val shaderBrush by remember {
        derivedStateOf {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && shader != null) {
                ShaderBrush(shader!!)
            } else {
                null
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        SketchWithCache(
            speed = if (static) 0f else 3f,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { time ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                shader?.setFloatUniform(
                    "iResolution",
                    this.size.width, this.size.height
                )
                shader?.setFloatUniform("iTime", time.value * 2f)
            }

            onDrawBehind {
                path.reset()

                if (shaderBrush != null && useShader) {
                    drawTrippySheep(
                        time = time,
                        noiseMax = noiseMax,
                        noiseType = noiseType,
                        showGuidelines = showGuidelines,
                        fluffBrush = shaderBrush!!
                    )
                } else {
                    drawTrippySheep(
                        time = time,
                        noiseMax = noiseMax,
                        noiseType = noiseType,
                        showGuidelines = showGuidelines,
                    )
                }
            }
        }

        LabeledText(
            modifier = Modifier.fillMaxWidth(),
            label = "Current Noise Type: ",
            body = noiseType.name,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LabeledText(
                modifier = Modifier.fillMaxWidth(),
                label = "Current Shader Type: ",
                body = if (useShader) shaderType.name else "None",
            )
        }

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (useShader) {
                        shaderType =
                            ShaderType.values().nextItemLoop(ShaderType.valueOf(shaderType.name))
                    }
                }
            ) {
                val text = "Change Shader Type"
                Text(text = text)
            }
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
                text = "Use shader",
                checked = useShader,
                onCheckedChange = { checked ->
                    useShader = checked
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
