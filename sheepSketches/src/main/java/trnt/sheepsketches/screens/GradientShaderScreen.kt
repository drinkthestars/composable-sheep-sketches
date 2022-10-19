package trnt.sheepsketches.screens

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import nstv.design.theme.ComposableSheepTheme
import nstv.sheep.model.FluffStyle
import nstv.sheep.model.Sheep
import nstv.sheep.parts.drawHead
import nstv.sheep.parts.drawLegs
import nstv.sheep.parts.getFluffPath
import nstv.sheep.parts.getFluffPoints
import com.canvas.sketch.SimpleSketchWithCache

@Composable
fun GradientShaderFluff(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }
    Box(modifier = modifier.fillMaxSize()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            GradientShaderFluffSheep(
                modifier = Modifier.fillMaxSize(),
                sheep = sheep
            )
        } else {
            Card(
                modifier = modifier
                    .wrapContentSize()
                    .padding(16.dp),
            ) {
                Text(text = "Shaders Fluff requires Android 13 (API 33)!")
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun GradientShaderFluffSheep(
    modifier: Modifier,
    sheep: Sheep,
    headColor: Color = sheep.headColor,
    legColor: Color = sheep.legColor,
    eyeColor: Color = sheep.eyeColor,
    glassesColor: Color = sheep.glassesColor,
    glassesTranslation: Float = sheep.glassesTranslation,
    showGuidelines: Boolean = false,
) {
    SimpleSketchWithCache(modifier = modifier) { time ->
        val circleRadius = size.width * 0.3f
        val circleCenterOffset = Offset(size.width / 2f, size.height / 2f)
        val fluffPoints: List<Offset> = getFluffPoints(
            fluffPercentages = sheep.fluffStyle.fluffChunksPercentages,
            radius = circleRadius,
            circleCenter = circleCenterOffset
        )
        val fluffPath = getFluffPath(
            fluffPoints = fluffPoints,
            circleRadius = circleRadius,
            circleCenterOffset = circleCenterOffset,
        )
        gradientShader.setFloatUniform(
            "iResolution",
            this.size.width, this.size.height
        )
        gradientShader.setFloatUniform("iTime", time.value)

        onDrawBehind {
            drawLegs(
                circleCenterOffset = circleCenterOffset,
                circleRadius = circleRadius,
                legs = sheep.legs,
                legColor = legColor,
                showGuidelines = showGuidelines
            )
            drawPath(path = fluffPath, brush = gradientShaderBrush)
            drawHead(
                circleCenterOffset = circleCenterOffset,
                circleRadius = circleRadius,
                headAngle = sheep.headAngle,
                headColor = headColor,
                eyeColor = eyeColor,
                glassesColor = glassesColor,
                glassesTranslation = glassesTranslation,
                showGuidelines = showGuidelines
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 320, backgroundColor = 0xFF292C34)
@Composable
private fun FlowFieldFluffSheepPreview() {
    ComposableSheepTheme {
        FlowFieldFluffSheep(
            modifier = Modifier.size(300.dp),
            sheep = Sheep(FluffStyle.Random()),
            fluffBrush = SolidColor(Color.LightGray),
            showGuidelines = false
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val gradientShader = RuntimeShader(
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val gradientShaderBrush = ShaderBrush(gradientShader)
