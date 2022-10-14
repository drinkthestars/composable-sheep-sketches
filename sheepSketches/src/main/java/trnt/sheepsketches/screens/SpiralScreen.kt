package trnt.sheepsketches.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.canvas.sketch.grids.HueNoisyUVMesh

@Composable
fun SpiralScreen(modifier: Modifier = Modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
        HueNoisyUVMesh(modifier = Modifier.fillMaxSize())
    }
}
