package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.canvas.sketch.Sketch
import com.canvas.sketch.map
import glm_.Detail.simplex
import glm_.vec2.Vec2
import nstv.sheep.model.FluffStyle
import nstv.sheep.parts.drawFluff
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs

const val TotalPercentage = 100.0f
const val MakeMoreRandom = true

@Composable
fun TrippyFluffAngles(modifier: Modifier = Modifier) {

    var fluffPoints by remember { mutableStateOf(FluffStyle.Random().fluffChunksPercentages) }

    Sketch(
        speed = 1f,
        modifier = modifier.fillMaxSize()
    ) { time ->
        val newFluffPoints = mutableListOf<Double>()
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
            newFluffPoints.add(angleChunk.toDouble())
            currentSum += angleChunk
            index++
        }

        fluffPoints = newFluffPoints

        drawLegs()

        drawFluff(
            fluffStyle = FluffStyle.Manual(fluffPoints),
        )

        drawHead()
    }
}
