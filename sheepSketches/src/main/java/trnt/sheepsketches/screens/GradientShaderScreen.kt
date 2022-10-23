package trnt.sheepsketches.screens

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ShaderBrush
import com.canvas.sketch.SketchWithCache
import glm_.value
import nstv.design.theme.Grid
import nstv.design.theme.TextUnit
import nstv.sheep.drawComposableSheep
import nstv.sheep.model.Sheep

@Composable
fun GradientShaderFluff(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onErrorContainer,
                contentColor = MaterialTheme.colorScheme.onError
            ),
            modifier = modifier.fillMaxWidth(),
        ) {
            Text(
                modifier = Modifier.padding(Grid.Two),
                fontSize = TextUnit.Sixteen,
                text = "Oh no! Gradient Shader Fluff requires Android 13 (API 33)!"
            )
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            GradientShaderFluffSheep(
                modifier = Modifier.fillMaxSize(),
                sheep = sheep
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GradientShaderFluffSheep(
    modifier: Modifier,
    sheep: Sheep,
    gradientShader: RuntimeShader = remember { gradientShader() },
    gradientShaderBrush: ShaderBrush = remember { ShaderBrush(gradientShader) },
) {
    SketchWithCache(
        speed = 10f,
        modifier = modifier
    ) { time ->
        gradientShader.setFloatUniform(
            "iResolution",
            this.size.width, this.size.height
        )
        gradientShader.setFloatUniform("iTime", time.value)

        onDrawBehind {
            drawComposableSheep(
                sheep = sheep,
                fluffBrush = gradientShaderBrush,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
internal fun gradientShader() = RuntimeShader(
    """
        uniform float2 iResolution;
        uniform float iTime;
        
        vec4 main(in float2 fragCoord) {
       
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord/iResolution.xy;
    
        // Time varying pixel color
        vec3 col = 0.8 + 0.2*cos(iTime*2.0+uv.xxx*2.0+vec3(1,2,4));
    
        // Output to screen
        return vec4(col,1.0);
       
    //    float uvY = fragCoord.y / iResolution.y;
    //    float progress = (uvY + sin((iTime) * 2.0*3.14)) / 3.0;
    //    vec3 color1 = vec3(46.0/255.0, 148.0/255.0, 250.0/255.0);
    //    vec3 color2 = vec3(1, 143.0/255.0, 216.0/255.0);
    //    return vec4(mix(color1, color2, progress), 1);
        }
    """
)
