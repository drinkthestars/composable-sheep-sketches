package com.canvas.sketch.grids

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.graphics.withSave
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.canvas.sketch.Sketch
import com.canvas.sketch.SketchWithCache
import com.canvas.sketch.TWO_PI
import com.canvas.sketch.capture.captureAndShare
import com.canvas.sketch.lerp
import com.canvas.sketch.map
import glm_.Java.Companion.glm
import glm_.glm.PIf
import glm_.glm.linearRand
import glm_.glm.pow
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.cos
import kotlin.math.sin

private val Padding = 48.dp
private const val DotCount = 10
private val paint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
    strokeWidth = 3f
    color = DarkGray
    alpha = 1f
}

@Composable
fun MonotoneNoisyUVMesh(modifier: Modifier = Modifier) {
    NoisyUVMesh(modifier) { dots: List<Offset>, dotCount: Int, path: Path ->
        drawMonotoneMeshPath(
            dots = dots,
            dotCount = dotCount,
            path = path
        )
    }
}

@Composable
fun HueNoisyUVMesh(modifier: Modifier = Modifier) {
    NoisyUVMesh(modifier) { dots: List<Offset>, dotCount: Int, path: Path ->
        drawMeshPathWithHue(
            dots = dots,
            dotCount = dotCount,
            path = path,
            width = size.width,
            height = size.height,
            hueMin = 200f,
            hueMax = 300f
        )
    }
}

@Composable
fun NoisyXYRectMesh(modifier: Modifier = Modifier) {
    NoisyXYMesh(modifier) { dots: List<Offset>, dotCount: Int, _, _ ->
        drawMeshRect(dots, dotCount, paint, rectSize = size.width / dotCount - 1f)
    }
}

@Composable
fun NoisyXYRandomRectMesh(modifier: Modifier = Modifier) {
    NoisyXYMesh(
        modifier,
        random = {
            linearRand(
                0f,
                1f
            )
        }
    ) { dots: List<Offset>, dotCount: Int, _, randoms: List<Float> ->
        drawMeshRect(dots, dotCount, paint, rectSize = size.width / dotCount - 1f, randoms)
    }
}

@Composable
fun MonotoneNoisyXYMesh(modifier: Modifier = Modifier) {
    NoisyXYMesh(modifier) { dots: List<Offset>, dotCount: Int, path: Path, _ ->
        drawMonotoneMeshPath(
            dots = dots,
            dotCount = dotCount,
            path = path
        )
    }
}

@Composable
fun HueNoisyXYMesh(modifier: Modifier = Modifier) {
    NoisyXYMesh(modifier) { dots: List<Offset>, dotCount: Int, path: Path, _ ->
        drawMeshPathWithHue(
            dots = dots,
            dotCount = dotCount,
            path = path,
            width = size.width,
            height = size.height,
            hueMin = 200f,
            hueMax = 300f
        )
    }
}

@Composable
fun NoisyPoints(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SketchWithCache(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
                .padding(Padding),
            onDrawWithCache = { time ->
                val (width, height) = this.size
                val dots = dots(
                    widthPx = size.width,
                    heightPx = size.height,
                    count = DotCount,
                    padding = with(density) { Padding.toPx() }
                )
                onDrawBehind {
                    val xyOffsets = dots.map { (u, v) ->
                        // lerp to get a value between 0 and 1
                        val posX = lerp(
                            min = 0f, max = width, norm = u
                        )
                        val posY = lerp(
                            min = 0f, max = height, norm = v
                        )

                        val noise = glm.simplex(
                            Vec4(
                                u, v, 10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
                            )
                        ) * 30f

                        Offset(x = posX + noise * 5f, y = posY + noise * 7f)
                    }

                    drawPoints(
                        color = Color.Black,
                        cap = StrokeCap.Round,
                        pointMode = PointMode.Points,
                        strokeWidth = 16f,
                        points = xyOffsets
                    )
                }
            }
        )
    }
}

@Composable
fun Random2DDots(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, random ->
        val rand = random ?: 1f
        drawCircle(
            color = DarkGray, radius = rand * 18f,
            center = Offset(
                x, y
            )
        )
    }
}

@Composable
fun Noise2DGrowingDots(modifier: Modifier = Modifier) {
    DotGrid(
        modifier,
        speed = 2f
    ) { u, v, x, y, time, dotCount, _ ->
        val noise = glm.simplex(Vec2(u, v)) * TWO_PI
        val radius = map(sin(time * 20f + noise * 10f), -1f, 1f, 3f, 13f)
        drawCircle(
            center = Offset(x, y), radius = radius, color = DarkGray
        )
    }
}

@Composable
fun Noise3DGrowingDots(modifier: Modifier = Modifier) {
    DotGrid(
        modifier,
        speed = 0.1f
    ) { u, v, x, y, time, dotCount, _ ->
        val noise = glm.simplex(Vec3(u, v, time)) * TWO_PI
        val radius = map(sin(time * 20f + noise * 10f), -1f, 1f, 3f, 13f)
        drawCircle(
            center = Offset(x, y), radius = radius, color = DarkGray
        )
    }
}

@Composable
fun Random2DGrowingDots(modifier: Modifier = Modifier) {
    DotGrid(
        modifier,
        speed = 2f,
    ) { u, v, x, y, time, dotCount, random ->
        val rand = random ?: 1f

        val radius = map(sin(time * 20f + rand * 10f), -1f, 1f, 3f, 13f)
        drawCircle(
            center = Offset(x, y), radius = radius, color = DarkGray
        )
    }
}

@Composable
fun DotStaticYRadiusVariation(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        drawCircle(
            color = DarkGray,
            radius = lerp(norm = v, min = 5f, max = 15f),
            center = Offset(x, y)
        )
    }
}

@Composable
fun DotStaticXRadiusVariation(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        drawCircle(
            color = DarkGray,
            radius = lerp(u, 5f, 15f),
            center = Offset(x, y)
        )
    }
}

@Composable
fun DotsStaticXYRadiusVariation(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        drawCircle(
            color = DarkGray,
            radius = lerp(u + v, 5f, 15f),
            center = Offset(x, y)
        )
    }
}

@Composable
fun DotYPendulum(modifier: Modifier = Modifier) {
    DotGrid(modifier, speed = 3f) { u, v, x, y, time, dotCount, _ ->
        val xShift = map(sin((u + time) * TWO_PI), -1f, 1f, -(100f) * v, (100f) * v)
        val shiftedX = xShift + x
        drawCircle(
            color = DarkGray, radius = y / size.width * 20f, center = Offset(shiftedX, y)
        )
    }
}

@Composable
fun DotYSpread(modifier: Modifier = Modifier) {
    DotGrid(modifier, speed = 3f) { u, v, x, y, time, dotCount, _ ->
        val xShiftMult = map(sin((u + v + time) * TWO_PI), -1f, 1f, 10f, 100f)
        val xShift = lerp(u, -xShiftMult * v, xShiftMult * v)
        val shiftedX = xShift + x
        drawCircle(
            color = DarkGray, radius = y / size.width * 20f, center = Offset(shiftedX, y)
        )
    }
}

@Composable
fun DotSinRadiusVariation(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        drawCircle(
            center = Offset(x, y),
            color = DarkGray,
            radius = map(
                sin(u * 10f + time * 20f),
                -1f, 1f,
                10f, 20f
            )
        )
    }
}

@Composable
fun DotAnimatedRadiusAndCenterVariation(modifier: Modifier = Modifier) {
    DotGrid(modifier, speed = 1f) { u, v, x, y, time, dotCount, _ ->
//        val noise = glm.simplex(
//            Vec4(
//                u, v, 10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
//            )
//        ) * 30f
//        drawCircle(z
//            color = DarkGray,
//            radius = map(sin(u), -1f, 1f, 5f, 15f),
//            center = offset
//        )
//
        drawCircle(
            color = DarkGray,
            radius = map(sin(u * PIf), -1f, 1f, 5f, 15f),
            center = Offset(x, y = y + map(sin(u * 300f), -1f, 1f, -10f, 100f))
        )

//
//        val offset = Offset(x, y + map(
//            sin(u * 300f + time * 10f),
//            -1f, 1f,
//            -10f, 100f
//        ))
//        drawCircle(
//            color = DarkGray,
//            radius = map(cos(u * 300f + time * 10f), -1f, 1f, 4f, 17f),
//            center = offset
//        )

        // Waves that increase with y
//        val sine = sin(u * 100f + time * 10f)
//        drawCircle(
//            color = DarkGray,
//            radius = map(sine, -1f, 1f, 8f, 14f),
//            center = Offset(x, y = y + map(sine, -1f, 1f, -10f, y/size.height * 200f))
//        )
    }
}

@Composable
fun Dot2DNoiseRadius(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        val noise2d = glm.simplex(Vec2(u * 5f, v * 5f))
        drawCircle(
            color = DarkGray,
            radius = map(noise2d, -1f, 1f, 3f, 17f),
            center = Offset(x, y)
        )
    }
}

@Composable
fun DotNoisyOffset(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, dotCount, _ ->
        val offset = Offset(x, y)
        val noise = glm.simplex(
            Vec4(
                u, v, 10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
            )
        ) * 30f
        val noisyOffset = offset.copy(y = y + noise, x = x - noise)
        val cosine = cos((u + v + (time * 10f) * TWO_PI))
        drawCircle(
            color = Color.hsl(
                hue = 200 + (cosine * 0.5f + 0.5f) * 100f,
                saturation = 1f,
                lightness = 0.2f + (cosine * 0.5f + 0.5f).coerceAtMost(0.8f)
            ),
            radius = map(cosine, -1f, 1f, 5f, 25f),
            center = noisyOffset
        )
    }
}

@Composable
fun Lines2DNoise(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, _, _, _ ->
        val r = 50f
        // noise = 0 to 1
        // take a fraction of 360 degrees or TWO_PI
        val startX = x - r / 2f
        val startY = y - r / 2f
        val radians = glm.simplex(Vec2(u, v)) * TWO_PI
        val endX = startX + (r * sin(radians))
        val endY = startY + (r * cos(radians))
        drawLine(
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 5f,
            color = DarkGray
        )
    }
}

@Composable
fun Lines3DNoise(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, _, _ ->
        // Lines 4D NOISE
        val r = 75f
        val startX = x - r / 2f
        val startY = y - r / 2f

        val radians = glm.simplex(
            Vec3(
                x = u,
                y = v,
                z = 5f * cos(TWO_PI * time / 20f)
            )
        ) * TWO_PI

        val endX = startX + (r * sin(radians))
        val endY = startY + (r * cos(radians))
        drawLine(
//                        cap = StrokeCap.Round,
            start = Offset(startX, startY),
            strokeWidth = 3f,
            end = Offset(endX, endY),
            color = Color.hsv(
                hue = map(
                    value = (startX + startY),
                    sourceMin = 0f,
                    sourceMax = (size.width + size.height),
                    destMin = 270f,
                    destMax = 320f
                ),
                saturation = 1f, value = 1f
            )
        )
    }
}

@Composable
fun Lines4DNoise(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, _, _ ->
        // Lines 4D NOISE
        val r = 55f
        val startX = x - r / 2f
        val startY = y - r / 2f

        val noise = glm.simplex(
            Vec4(
                x = u,
                y = v,
                z = 5f * cos(TWO_PI * time / 5f),
                w = 5f * sin(TWO_PI * time / 5f)
            )
        )
        val radians = noise * TWO_PI

        val endX = startX + (r * sin(radians))
        val endY = startY + (r * cos(radians))
//        val hue = (noise * 360f).absoluteValue
        val hue = map(noise, -1f, 1f, 170f, 300f)
        val color = Color.hsv(
            hue = hue, saturation = 1F, value = 1f
        )
        drawLine(
            start = Offset(startX, startY),
            strokeWidth = 4f,
            end = Offset(endX, endY),
            color = color
        )
    }
}

@Composable
fun DotAnimatedRadiusAndOffset(modifier: Modifier = Modifier) {
    DotGrid(modifier, speed = 2f) { u, v, x, y, time, _, _ ->
//        10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
        drawCircle(
            color = DarkGray,
            center = Offset(
                x = map(
                    cos(TWO_PI * time + u * 30f),
                    -1f, 1f,
                    -50f, 200f
                ),
                y = map(
                    sin(TWO_PI * time + v * 30f),
                    -1f, 1f,
                    -50f, 200f
                )
            ),
            radius = map(
                sin(x * 10f + time * 10f),
                -1f, 1f,
                6f, 15f
            ),
        )

//        drawCircle(
//            color = DarkGray,
//            center = Offset(
//                x = x,
//                y = x + map(
//                    sin(x * 190f + time * 20f),
//                    -1f, 1f,
//                    -50f, 200f
//                )
//            ),
//            radius = map(
//                sin(x * 10f + time * 10f),
//                -1f, 1f,
//                10f, 25f
//            ),
//        )
    }
}

@Composable
fun DotParametric(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, _, _ ->
        drawCircle(
            color = DarkGray,
            center = Offset(
                x = x,
                y = y + map(
                    sin(y * 190f + time * 20f) + cos(y * 40f + time * 30f),
                    -2f, 2f,
                    -50f, 200f
                )
            ),
            radius = map(
                sin(y * 20f + time * 20f) + cos(x * 100f + time * 30f),
                -2f, 2f,
                6f, 15f
            ),
        )
    }
}

@Composable
fun DotsAroundCircleWavy(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, _, _ ->
        val isInCircle = isInCircle(x, y)

        val shiftedX = x + map(
            sin((u + time * 30) * TWO_PI),
            -1f, 1f,
            -20f, 20f
        )
        val effectiveX = if (isInCircle) shiftedX else x
        val effectiveRad = if (isInCircle) y / size.width * 15f else 5f
        val color = Color.hsv(
            hue = map(
                value = (effectiveRad),
                sourceMin = 0f,
                sourceMax = 15f,
                destMin = 130f,
                destMax = 230f
            ),
            saturation = 1f, value = 1f
        )
        val effectiveColor = if (isInCircle) color else DarkGray
        drawCircle(
            color = effectiveColor, radius = effectiveRad, center = Offset(effectiveX, y)
        )
    }
}

@Composable
fun DotsAroundCircleHalftones(modifier: Modifier = Modifier) {
    DotGrid(modifier) { u, v, x, y, time, _, _ ->
        val sineRad = map(
            sin((v + time * 30) * TWO_PI),
            -1f, 1f,
            6f, 15f
        )
        val isInCircle = isInCircle(x, y)
        val effectiveRad = if (isInCircle) sineRad else 5f
        val color = Color.hsv(
            hue = map(
                value = effectiveRad,
                sourceMin = 0f,
                sourceMax = 15f,
                destMin = 200f,
                destMax = 300f
            ),
            saturation = v, value = 1f
        )
        val effectiveColor = if (isInCircle) color else DarkGray
        drawCircle(
            color = effectiveColor, radius = effectiveRad, center = Offset(x, y)
        )
    }
}

@Composable
private fun NoisyXYMesh(
    modifier: Modifier = Modifier,
    random: (() -> Float)? = null,
    onDrawMesh: DrawScope.(dots: List<Offset>, dotCount: Int, path: Path, randoms: List<Float>) -> Unit
) {
    GridContainer(modifier) { dotCount, onGloballyPositioned ->
        val randoms = random?.run { List(pow(dotCount, 2)) { invoke() } } ?: emptyList()

        SketchWithCache(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDrawWithCache = { time ->
                val path = Path()
                val (width, height) = this.size
                val dots = dots(
                    widthPx = size.width,
                    heightPx = size.height,
                    count = dotCount,
                    padding = with(density) { Padding.toPx() }
                )
                onDrawBehind {
                    path.reset()
                    val xyOffsets = dots.map { (u, v) ->
                        // lerp to get a value between 0 and 1
                        val posX = lerp(
                            min = 0f, max = width, norm = u
                        )
                        val posY = lerp(
                            min = 0f, max = height, norm = v
                        )

                        val noise = glm.simplex(
                            Vec4(
                                posX,
                                posY,
                                10f * cos(TWO_PI * time / 12),
                                10f * sin(TWO_PI * time / 10)
                            )
                        ) * dotCount * 3
                        Offset(x = posX - noise, y = posY + noise)
                    }

                    onDrawMesh(xyOffsets, dotCount, path, randoms)
                }
            }
        )
    }
}

@Composable
private fun NoisyUVMesh(
    modifier: Modifier = Modifier,
    onDrawMesh: DrawScope.(dots: List<Offset>, dotCount: Int, path: Path) -> Unit
) {
    GridContainer(modifier) { dotCount, onGloballyPositioned ->
        SketchWithCache(
            modifier = Modifier
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDrawWithCache = { time ->
                val path = Path()
                val (width, height) = this.size
                val dots = dots(
                    widthPx = size.width,
                    heightPx = size.height,
                    count = dotCount,
                    padding = with(density) { Padding.toPx() }
                )
                onDrawBehind {
                    path.reset()
                    val xyOffsets = dots.map { (u, v) ->
                        // lerp to get a value between 0 and 1
                        val posX = lerp(
                            min = 0f, max = width, norm = u
                        )
                        val posY = lerp(
                            min = 0f, max = height, norm = v
                        )

                        val noise = glm.simplex(
                            Vec4(
                                u, v, 10f * cos(TWO_PI * time / 12), 10f * sin(TWO_PI * time / 10)
                            )
                        ) * 30f
                        Offset(x = posX - noise, y = posY + noise)
                    }

                    onDrawMesh(xyOffsets, dotCount, path)
                }
            }
        )
    }
}

@Composable
private fun DotGrid(
    modifier: Modifier = Modifier,
    speed: Float = 0.1f,
    onDraw: DrawScope.(u: Float, v: Float, x: Float, y: Float, time: Float, dotCount: Float, random: Float?) -> Unit
) {
    var randoms by remember { mutableStateOf(emptyList<Float>()) }
    GridContainer(modifier, onRefreshClick = { dotCount ->
        randoms = List(pow(dotCount, 2)) { linearRand(0f, 1f) }
    }) { dotCount, onGloballyPositioned ->
        randoms = List(pow(dotCount, 2)) { linearRand(0f, 1f) }
        Sketch(
            speed = speed,
            modifier = Modifier
                .fillMaxSize(0.9f)
                .aspectRatio(1f)
//                .border(1.dp, DarkGray)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding)
        ) { time ->
            val (width, height) = this.size
            (0 until dotCount).forEach { x ->
                (0 until dotCount).forEach { y ->
                    // working between 0 and 1 aka U/V Space
                    val u = x / (dotCount - 1).toFloat()
                    val v = y / (dotCount - 1).toFloat()

                    // lerp to get a value between 0 and 1
                    val posX = lerp(
                        min = 0f, max = width, norm = u
                    )
                    val posY = lerp(
                        min = 0f, max = height, norm = v
                    )

                    onDraw(
                        u,
                        v,
                        posX,
                        posY,
                        time,
                        dotCount.toFloat(),
                        randoms.getOrNull(x + y * dotCount)
                    )
                }
            }
        }
    }
}

@Composable
private fun GridContainer(
    modifier: Modifier = Modifier,
    onRefreshClick: (Int) -> Unit = {},
    content: @Composable BoxScope.(dotCount: Int, onGloballyPositioned: (LayoutCoordinates) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var dotCount by remember { mutableStateOf(10) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }

    Box(
        modifier = modifier
            .padding(top = 100.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = dotCount.toFloat(),
                valueRange = 3f..50f,
                onValueChange = { dotCount = it.toInt() }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    captureAndShare(
                        width = size.width,
                        height = size.height,
                        rect = boundsInWindow.toAndroidRect(),
                        context = context
                    )
                }) {
                    Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
                }
                IconButton(onClick = { onRefreshClick(dotCount) }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                }
                Spacer(Modifier.width(10.dp))
                Text(text = "Dot count: $dotCount")
            }
        }
        content(dotCount) {
            size = it.size
            boundsInWindow = it.boundsInWindow()
        }
    }
}

private fun DrawScope.drawMonotoneMeshPath(
    dots: List<Offset>,
    dotCount: Int,
    path: Path
) {
    dots.forEachIndexed { i, offset ->
        val x = i % dotCount
        val y = i / dotCount
        if (x > 0 && y > 0) {
            makePathForMesh(path, x, y, dotCount, dots, offset)
            drawMonotoneGrid(path)
        }
    }
}

private fun DrawScope.drawMeshPathWithHue(
    dots: List<Offset>,
    dotCount: Int,
    path: Path,
    width: Float,
    height: Float,
    hueMin: Float,
    hueMax: Float
) {
    dots.forEachIndexed { i, offset ->
        val x = i % dotCount
        val y = i / dotCount
        if (x > 0 && y > 0) {
            makePathForMesh(path, x, y, dotCount, dots, offset)
            drawPathWithHue(
                offset = offset,
                width = width,
                height = height,
                path = path,
                hueMin = hueMin,
                hueMax = hueMax
            )
        }
    }
}

private fun makePathForMesh(
    path: Path,
    x: Int,
    y: Int,
    dotCount: Int,
    dots: List<Offset>,
    offset: Offset
) {
    path.reset()
    val topLeftIndx = ((x - 1) + (y - 1) * dotCount)
    val topRightIndx = ((x) + (y - 1) * dotCount)
    val bottomLeftIndx = ((x - 1) + (y) * dotCount)
    val topLeft = dots[topLeftIndx]
    val topRight = dots[topRightIndx]
    val bottomLeft = dots[bottomLeftIndx]
    path.moveTo(offset.x, offset.y)
    path.lineTo(topRight.x, topRight.y)
    path.lineTo(topLeft.x, topLeft.y)
    path.lineTo(bottomLeft.x, bottomLeft.y)
    path.lineTo(offset.x, offset.y)
    path.close()
}

private fun DrawScope.drawMonotoneGrid(path: Path) {
    drawPath(
        color = DarkGray, style = Stroke(3f), path = path
    )
}

private fun DrawScope.drawPathWithHue(
    offset: Offset,
    width: Float,
    height: Float,
    path: Path,
    hueMin: Float = 170f,
    hueMax: Float = 300f
) {
    drawPath(
        color = Color.hsv(
            hue = map(
                value = (offset.x + offset.y),
                sourceMin = 0f,
                sourceMax = (width + height),
                destMin = hueMin,
                destMax = hueMax
            ),
            saturation = 0.7f, value = 1f
        ),
        path = path
    )
}

private fun DrawScope.drawMeshRect(
    dots: List<Offset>,
    dotCount: Int,
    paint: Paint,
    rectSize: Float,
    random: List<Float> = emptyList()
) {
    dots.forEachIndexed { i, offset ->
        val x = i % dotCount
        val y = i / dotCount
        if (x < size.width && y < size.height) {

            if (random.isNotEmpty()) {
                check(
                    random.size == pow(
                        dotCount,
                        2
                    )
                ) {
                    "size of random values ${random.size} must be = dotCount ^ 2 (aka ${
                        pow(
                            dotCount,
                            2
                        )
                    }!"
                }
                if (random[i] > 0.5f) {
                    paint.style = PaintingStyle.Stroke
                } else {
                    paint.style = PaintingStyle.Fill
                }
            }

            drawContext.canvas.nativeCanvas.drawRect(
                offset.x, offset.y, offset.x + rectSize, offset.y + rectSize,
                paint.asFrameworkPaint()
            )
        }
    }
}

private fun DrawScope.drawRotatedText(
    dot: Offset,
    paint: NativePaint,
    time: Float,
    size: Float = 15f
) {
    drawIntoCanvas { canvas ->
        canvas.withSave {
            canvas.nativeCanvas.rotate(
                map(sin(time), -1f, 1f, -PIf, PIf),
                dot.x,
                dot.y,
            )
            canvas.nativeCanvas.drawText(
                "|", dot.x, dot.y,
                paint.apply {
                    textSize = size * 2f + time
                }
            )
        }
    }
}

private fun dots(
    widthPx: Float,
    heightPx: Float,
    count: Int,
    padding: Float
): List<Offset> {
    return mutableListOf<Offset>().apply {
        (0 until count).forEach { x ->
            (0 until count).forEach { y ->
                // working between 0 and 1 aka U/V Space
                val u = x / (count - 1).toFloat()
                val v = y / (count - 1).toFloat()

                // lerp to get a value between 0 and 1
                val posX = lerp(padding, widthPx - padding, u)
                val posY = lerp(padding, heightPx - padding, v)

                val uvOffset = Offset(u, v)

                add(uvOffset)
            }
        }
    }
}

private fun DrawScope.isInCircle(
    x: Float,
    y: Float,
    radius: Float = size.width / 4f
): Boolean {
    val distToCentSqrd = pow(x - center.x, 2f) + pow(y - center.y, 2f)
    return distToCentSqrd < pow(radius, 2f)
}
