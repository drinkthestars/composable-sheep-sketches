package com.canvas.sketch

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toAndroidRect
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.canvas.sketch.capture.captureAndShare
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

private val AnimationSpec = tween<Float>(5000, 0, easing = CubicBezierEasing(.46f, 0.17f, .29f, .23f))

@Composable
fun RedrawCanvas(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    onDraw: DrawScope.(Float) -> Unit
) {
    val drawParamsRef = remember { mutableStateOf<DrawParams?>(null) }
    val advance = remember { AnimationState(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            advance.animateTo(
                targetValue = advance.value + speed,
                animationSpec = animationSpec,
                sequentialAnimation = true
            )
        }
    }
    Box(
        modifier = modifier
            .drawWithCache {
                val canvas: Canvas
                val drawScope: CanvasDrawScope
                val imageBitmap: ImageBitmap
                var drawParams = drawParamsRef.value
                // Create offscreen bitmap if we don't have one. Note this should also be updated
                // if the size changes. This bitmap is persisted across composition calls and retains
                // the previously drawn lines across each composition
                if (drawParams == null) {
                    imageBitmap = ImageBitmap(size.width.roundToInt(), size.height.roundToInt())
                    canvas = Canvas(imageBitmap)
                    drawScope = CanvasDrawScope()
                    drawParams = DrawParams(imageBitmap, canvas, drawScope)
                    drawParamsRef.value = drawParams
                }
                drawParams.drawScope.draw(
                    density = this,
                    layoutDirection = layoutDirection,
                    canvas = drawParams.canvas,
                    size = size
                ) {
                    onDraw(advance.value)
                }

                onDrawBehind {
                    // After the bitmap is updated, draw the bitmap once here
                    drawImage(drawParams.imageBitmap)
                }
            }
    )
}

@Composable
fun SketchWithCache(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    showControls: Boolean = false,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    onDrawWithCache: CacheDrawScope.(Float) -> DrawResult
) {
    val fps = fps()
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val advance = remember { AnimationState(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            advance.animateTo(
                targetValue = advance.value + speed,
                animationSpec = animationSpec,
                sequentialAnimation = true
            )
        }
    }

    if (showControls) {
        Column(
            modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    }
                    .drawWithCache {
                        onDrawWithCache(advance.value)
                    }
            )
            Controls(fps, size, boundsInWindow)
        }
    } else {
        Box(
            modifier = modifier.drawWithCache {
                onDrawWithCache(advance.value)
            }
        )
    }
}

@Composable
fun Sketch(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    showControls: Boolean = false,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    onDraw: DrawScope.(Float) -> Unit
) {
    val fps = fps()
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val advance = remember { AnimationState(0f) }

    LaunchedEffect(Unit) {
        while (isActive) {
            advance.animateTo(
                targetValue = advance.value + speed,
                animationSpec = animationSpec,
                sequentialAnimation = true
            )
        }
    }

    if (showControls) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Canvas(
                modifier = modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    },
            ) {
                onDraw(advance.value)
            }
            Controls(fps, size, boundsInWindow)
        }
    } else {
        Canvas(modifier = modifier) {
            onDraw(advance.value)
        }
    }
}

@Composable
fun RedrawSketch(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    showControls: Boolean = false,
    animationSpec: AnimationSpec<Float> = AnimationSpec,
    onDraw: DrawScope.(Float) -> Unit
) {
    val fps = fps()
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }

    if (showControls) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            RedrawCanvas(
                modifier = modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    },
                onDraw = onDraw,
                speed = speed,
                animationSpec = animationSpec
            )
            Controls(fps, size, boundsInWindow)
        }
    } else {
        RedrawCanvas(
            modifier = modifier,
            onDraw = onDraw,
            speed = speed,
            animationSpec = animationSpec
        )
    }
}

@Composable
private fun Controls(
    fps: State<Long>,
    size: IntSize,
    boundsInWindow: Rect
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .wrapContentSize()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
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
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "fps = ${fps.value}",
            color = Color.Black,
        )
    }
}

data class DrawParams(
    val imageBitmap: ImageBitmap,
    val canvas: Canvas,
    val drawScope: CanvasDrawScope
)
