package trnt.sheepsketches.draw

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache

@Composable
fun SketchWithCache(
    modifier: Modifier = Modifier,
    speed: Float = 0.01f,
    onBuildDrawCache: CacheDrawScope.(time: State<Float>) -> DrawResult
) {
    val time = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        do {
            withFrameMillis {
                time.value = time.value + speed
            }
        } while (true)
    }

    Box(modifier = modifier.drawWithCache { onBuildDrawCache(time) })
}
