package trnt.sheepsketches.screens

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
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
import com.canvas.sketch.SimpleSketchWithCache
import nstv.design.theme.Grid
import nstv.design.theme.TextUnit
import nstv.sheep.drawComposableSheep
import nstv.sheep.model.Sheep

@Composable
fun BackgroundShaderScreen(modifier: Modifier = Modifier) {
    val sheep = remember { Sheep() }
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
                text = "Oh no! Gradient Shader Fluff requires Android 13 (API 33)!"
            )
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            EtherealShaderScreenSheep(
                modifier = Modifier.fillMaxSize(),
                sheep = sheep
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun EtherealShaderScreenSheep(
    modifier: Modifier,
    sheep: Sheep,
    gradientShader: RuntimeShader = remember { warpSpeedShader() },
    gradientShaderBrush: ShaderBrush = remember { ShaderBrush(gradientShader) },
) {
    SimpleSketchWithCache(speed = 0.08f, modifier = modifier) { time ->
        gradientShader.setFloatUniform(
            "iResolution",
            this.size.width, this.size.height
        )
        gradientShader.setFloatUniform("iTime", time.value)

        onDrawBehind {
            drawRect(brush = gradientShaderBrush)
            drawComposableSheep(sheep = sheep)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun warpSpeedShader() = RuntimeShader(
    """
        // 'Warp Speed 2'
        // David Hoskins 2015.
        // License Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.

        // Fork of:-   https://www.shadertoy.com/view/Msl3WH
        //----------------------------------------------------------------------------------------
        uniform float2 iResolution;      // Viewport resolution (pixels)
        uniform float  iTime;            // Shader playback time (s)

        vec4 main( in float2 fragCoord )
        {
            float s = 0.0, v = 0.0;
            vec2 uv = (fragCoord / iResolution.xy) * 2.0 - 1.;
            float time = (iTime-2.0)*58.0;
            vec3 col = vec3(0);
            vec3 init = vec3(sin(time * .0032)*.3, .35 - cos(time * .005)*.3, time * 0.002);
            for (int r = 0; r < 100; r++) 
            {
                vec3 p = init + s * vec3(uv, 0.05);
                p.z = fract(p.z);
                // Thanks to Kali's little chaotic loop...
                for (int i=0; i < 10; i++)	p = abs(p * 2.04) / dot(p, p) - .9;
                v += pow(dot(p, p), .7) * .06;
                col +=  vec3(v * 0.2+.4, 12.-s*2., .1 + v * 1.) * v * 0.00003;
                s += .025;
            }
            return vec4(clamp(col, 0.0, 1.0), 1.0);
        }
    """.trimIndent()
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun lightScatteringShader() = RuntimeShader(
    """
        uniform float2 iResolution;      // Viewport resolution (pixels)
        uniform float  iTime;            // Shader playback time (s)
        uniform float4 iMouse;           // Mouse drag pos=.xy Click pos=.zw (pixels)
        
        //Based on Naty Hoffmann and Arcot J. Preetham. Rendering out-door light scattering in real time.
        //http://renderwonk.com/publications/gdm-2002/GDM_August_2002.pdf
        
        const float fov = tan(radians(60.0));
        const float cameraheight = 5e1; //50.
        const float Gamma = 2.2;
        const float Rayleigh = 1.;
        const float Mie = 1.;
        const float RayleighAtt = 1.;
        const float MieAtt = 1.2;

        float g = -0.9;
        
        vec3 _betaR = vec3(1.95e-2, 1.1e-1, 2.94e-1); 
        vec3 _betaM = vec3(4e-2, 4e-2, 4e-2);
        
        const float ts= (cameraheight / 2.5e5);
        
        vec3 Ds = normalize(vec3(0., 0., -1.)); //sun 
        
        vec3 ACESFilm( vec3 x )
        {
            float tA = 2.51;
            float tB = 0.03;
            float tC = 2.43;
            float tD = 0.59;
            float tE = 0.14;
            return clamp((x*(tA*x+tB))/(x*(tC*x+tD)+tE),0.0,1.0);
        }
        
        vec4 main(in float2 fragCoord ) {
        
            float AR = iResolution.x/iResolution.y;
            float M = 1.0; //canvas.innerWidth/M //canvas.innerHeight/M --res
            
            vec2 uvMouse = (iMouse.xy / iResolution.xy);
            uvMouse.x *= AR;
            
            vec2 uv0 = (fragCoord.xy / iResolution.xy);
            uv0 *= M;
            //uv0.x *= AR;
            
            vec2 uv = uv0 * (2.0*M) - (1.0*M);
            uv.x *=AR;
            
            if (uvMouse.y == 0.) uvMouse.y=(0.7-(0.05*fov)); //initial view 
            if (uvMouse.x == 0.) uvMouse.x=(1.0-(0.05*fov)); //initial view
            
            Ds = normalize(vec3(uvMouse.x-((0.5*AR)), uvMouse.y-0.5, (fov/-2.0)));
            
            vec3 O = vec3(0., cameraheight, 0.);
            vec3 D = normalize(vec3(uv, -(fov*M)));
        
            vec3 color = vec3(0.);
            
            if (D.y < -ts) {
                float L = - O.y / D.y;
                O = O + D * L;
                D.y = -D.y;
                D = normalize(D);
            }
            else{
                float L1 =  O.y / D.y;
                vec3 O1 = O + D * L1;
        
                vec3 D1 = vec3(1.);
                D1 = normalize(D);
            }
            
              float t = max(0.001, D.y) + max(-D.y, -0.001);
        
              // optical depth -> zenithAngle
              float sR = RayleighAtt / t ;
              float sM = MieAtt / t ;
        
              float cosine = clamp(dot(D,Ds),0.0,1.0);
              vec3 extinction = exp(-(_betaR * sR + _betaM * sM));
        
               // scattering phase
              float g2 = g * g;
              float fcos2 = cosine * cosine;
              float miePhase = Mie * pow(1. + g2 + 2. * g * cosine, -1.5) * (1. - g2) / (2. + g2);
                //g = 0;
              float rayleighPhase = Rayleigh;
        
              vec3 inScatter = (1. + fcos2) * vec3(rayleighPhase + _betaM / _betaR * miePhase);
        
              color = inScatter*(1.0-extinction); // *vec3(1.6,1.4,1.0)
        
                // sun
              color += 0.47*vec3(1.6,1.4,1.0)*pow( cosine, 350.0 ) * extinction;
              // sun haze
              color += 0.4*vec3(0.8,0.9,1.0)*pow( cosine, 2.0 )* extinction;
            
              color = ACESFilm(color);
            
              color = pow(color, vec3(Gamma));
            
              return vec4(color, 1.);
        }
    """
)
