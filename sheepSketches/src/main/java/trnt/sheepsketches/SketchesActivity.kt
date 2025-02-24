package trnt.sheepsketches

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import nstv.design.theme.ComposableSheepTheme
import nstv.design.theme.Grid
import trnt.sheepsketches.Screen.FLOW_FIELD_FLUFF
import trnt.sheepsketches.Screen.FLUFF_ANGLES
import trnt.sheepsketches.Screen.FLUFF_PATH
import trnt.sheepsketches.Screen.GAUSSIAN_GRID_FLUFF
import trnt.sheepsketches.Screen.GRADIENT_SHADER
import trnt.sheepsketches.Screen.BG_SHADER
import trnt.sheepsketches.Screen.GRID_FLUFF
import trnt.sheepsketches.Screen.LINE_ANGLES_FLUFF
import trnt.sheepsketches.Screen.SPIRAL
import trnt.sheepsketches.Screen.values
import trnt.sheepsketches.screens.BackgroundShaderScreen
import trnt.sheepsketches.screens.FlowFieldFluff
import trnt.sheepsketches.screens.GaussianGridFluff
import trnt.sheepsketches.screens.GradientShaderFluff
import trnt.sheepsketches.screens.GridFluffScreen
import trnt.sheepsketches.screens.LineAnglesFluffScreen
import trnt.sheepsketches.screens.SpiralScreen
import trnt.sheepsketches.screens.TrippyFluffAngles
import trnt.sheepsketches.screens.TrippyFluffPath

private enum class Screen {
    SPIRAL,
    GRID_FLUFF,
    GAUSSIAN_GRID_FLUFF,
    LINE_ANGLES_FLUFF,
    FLOW_FIELD_FLUFF,
    FLUFF_ANGLES,
    FLUFF_PATH,
    GRADIENT_SHADER,
    BG_SHADER,
}

private val InitialScreen = FLUFF_PATH

class SketchesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposableSheepTheme {

                val systemUiController = rememberSystemUiController()
                val useDarkIcons = isSystemInDarkTheme().not()
                val surfaceColor = MaterialTheme.colorScheme.surface

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = surfaceColor, darkIcons = useDarkIcons
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    var expanded by remember { mutableStateOf(false) }
                    var selectedScreen by remember { mutableStateOf(InitialScreen) }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(Grid.Two)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                            Row(modifier = Modifier.clickable { expanded = true }) {
                                Text(
                                    text = selectedScreen.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                )
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Select Screen"
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                values().forEach { screen ->
                                    DropdownMenuItem(
                                        text = { Text(text = screen.name) },
                                        onClick = {
                                            expanded = false
                                            selectedScreen = screen
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.padding(vertical = Grid.One))

                        Crossfade(
                            targetState = selectedScreen,
                            animationSpec = tween(durationMillis = 500)
                        ) { screen ->
                            when (screen) {
                                SPIRAL -> SpiralScreen()
                                GRID_FLUFF -> GridFluffScreen()
                                GAUSSIAN_GRID_FLUFF -> GaussianGridFluff()
                                LINE_ANGLES_FLUFF -> LineAnglesFluffScreen()
                                FLOW_FIELD_FLUFF -> FlowFieldFluff()
                                FLUFF_ANGLES -> TrippyFluffAngles()
                                FLUFF_PATH -> TrippyFluffPath()
                                GRADIENT_SHADER -> GradientShaderFluff()
                                BG_SHADER -> BackgroundShaderScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
