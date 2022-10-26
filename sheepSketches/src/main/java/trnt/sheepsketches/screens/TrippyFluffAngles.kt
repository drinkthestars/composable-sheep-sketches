package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import com.canvas.sketch.Sketch
import com.canvas.sketch.map
import glm_.Detail.simplex
import glm_.vec2.Vec2
import nstv.canvasExtensions.maths.TotalPercentage
import nstv.canvasExtensions.nextItemLoop
import nstv.design.theme.Grid
import nstv.design.theme.SheepColor
import nstv.design.theme.components.LabeledText
import nstv.sheep.model.FluffStyle
import nstv.sheep.parts.drawFluff
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import trnt.sheepsketches.screens.ChangeStyle.PairNones
import trnt.sheepsketches.screens.ChangeStyle.Simplex
import trnt.sheepsketches.screens.ChangeStyle.valueOf
import trnt.sheepsketches.screens.ChangeStyle.values
import kotlin.math.sin

private const val MakeMoreRandom = true

private enum class ChangeStyle {
    Simplex,
    PairNones,
}

private const val FluffMinAnglePercentage = 5.0
private const val Speed = 1f
private const val FullLoopTime = 0.5f * Speed

@Composable
fun TrippyFluffAngles(modifier: Modifier = Modifier) {

    var fluffChunksPercentages by remember { mutableStateOf(FluffStyle.Random().fluffChunksPercentages) }
    var changeStyle by remember { mutableStateOf(PairNones) }

    Column(modifier = modifier.verticalScroll(rememberScrollState())) {
        Sketch(
            speed = Speed,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) { time ->
            fluffChunksPercentages = when (changeStyle) {
                Simplex -> {
                    getFluffChunksWithSimplex(time)
                }
                PairNones -> {
                    getFluffChunksWithPairNones(time)
                }
            }

            drawLegs()

            drawFluff(
                fluffStyle = FluffStyle.Manual(fluffChunksPercentages),
                fluffBrush = SolidColor(if (changeStyle == Simplex) SheepColor.Purple else SheepColor.Green)
            )

            drawHead()
        }
        LabeledText(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Grid.One),
            label = "Current Change Style: ",
            body = changeStyle.name,
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                changeStyle =
                    values().nextItemLoop(valueOf(changeStyle.name))
            }
        ) {
            val text = "Change style"
            Text(text = text)
        }
    }
}

private fun getFluffChunksWithSimplex(
    time: Float
): List<Double> {
    val newFluffChunksPercentages = mutableListOf<Double>()
    var currentSum = 0f
    var index = 0
    while (currentSum < TotalPercentage) {

        val noise = simplex(
            if (MakeMoreRandom) {
                Vec2(index + time)
            } else {
                Vec2(index, time)
            }
        )

        var angleChunk = map(noise, -1f, 1f, 5f, 20f)

        if (currentSum + angleChunk > TotalPercentage) {
            angleChunk = TotalPercentage - currentSum
        }
        newFluffChunksPercentages.add(angleChunk.toDouble())
        currentSum += angleChunk
        index++
    }

    return newFluffChunksPercentages
}

private fun getFluffChunksWithPairNones(
    time: Float,
    numberOfPairs: Int = 6,
    minAnglePercentage: Float = FluffMinAnglePercentage.toFloat()
): List<Double> {
    val newFluffChunksPercentages = mutableListOf<Double>()
    val maxAnglePercentage = TotalPercentage / numberOfPairs - minAnglePercentage
    val numberOfFluffChunks = numberOfPairs * 2

    for (index in 0 until numberOfFluffChunks) {
        val mappedFakeAngle = map(
            time.mod(FullLoopTime) / FullLoopTime,
            0f,
            1f,
            0f,
            Math.PI.times(2).toFloat()
        )

        val valueChange = if (index % 2 == 0) {
            sin(mappedFakeAngle)
        } else {
            sin(mappedFakeAngle + Math.PI.toFloat())
        }

        val newPercentage =
            map(
                valueChange,
                -1f,
                1f,
                minAnglePercentage,
                maxAnglePercentage,
            )
        newFluffChunksPercentages.add(newPercentage.toDouble())
    }

    return newFluffChunksPercentages
}

@Preview
@Composable
private fun TrippyFluffAnglesPreview() {
    TrippyFluffAngles()
}
