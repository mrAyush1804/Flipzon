package com.flipzon.app.feature.splash
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun TwinLinesAnimationWrapper(
    content: @Composable () -> Unit,
    Logo: String,
) {
    val linesHeightFrac = remember { Animatable(0f) }
    val linesAlpha = remember { Animatable(0f) }
    val gapPxAnim = remember { Animatable(0f) }


    val logoAlpha = remember { Animatable(0f) }
    val logoScale = remember { Animatable(0.8f) }

    val density = LocalDensity.current
    var isDone by remember { mutableStateOf(false) }
    val lineWidthPx = with(density) { 3.dp.toPx() }

    LaunchedEffect(Unit) {

        launch { logoAlpha.animateTo(1f, tween(1200)) }
        logoScale.animateTo(1f, tween(1200, easing = FastOutSlowInEasing))

        delay(500) // Logo ko thodi der dikhne dein


        launch { linesAlpha.animateTo(1f, tween(800)) }
        launch { logoAlpha.animateTo(0.3f, tween(800)) } // Logo thoda halka ho jayega


        linesHeightFrac.animateTo(1f, tween(1500, easing = CubicBezierEasing(0.18f, 0f, 0f, 1f)))


        launch { logoAlpha.animateTo(0f, tween(600)) }
        launch { logoScale.animateTo(1.2f, tween(600)) } // Logo halka sa zoom hokar gayab hoga

        delay(500)
        gapPxAnim.animateTo(4000f, tween(3000, easing = CubicBezierEasing(0.10f, 0.92f, 0.16f, 1f)))


        linesAlpha.animateTo(0f, tween(1000))
        isDone = true
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        content()

        if (!isDone) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w / 2f
                val currentGap = gapPxAnim.value.coerceAtMost(w)

                val leftEdge = (cx - currentGap / 2f).coerceAtLeast(0f)
                val rightEdge = (cx + currentGap / 2f).coerceAtMost(w)

                // Black Curtain
                drawRect(Color.White, Offset.Zero, Size(leftEdge, h))
                drawRect(Color.White, Offset(rightEdge, 0f), Size(w - rightEdge, h))

                // Twin Lines
                if (linesAlpha.value > 0f) {
                    val heightFrac = linesHeightFrac.value
                    val halfHeight = (h * heightFrac) / 2f
                    val top = (h / 2f) - halfHeight
                    val bottom = (h / 2f) + halfHeight

                    drawLine(Color.Black.copy(alpha = linesAlpha.value), Offset(leftEdge, top), Offset(leftEdge, bottom), strokeWidth = lineWidthPx)
                    drawLine(Color.Black.copy(alpha = linesAlpha.value), Offset(rightEdge, top), Offset(rightEdge, bottom), strokeWidth = lineWidthPx)
                }
            }


            if (logoAlpha.value > 0f) {
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            this.scaleX = logoScale.value
                            this.scaleY = logoScale.value
                            this.alpha = logoAlpha.value
                        }
                ) {

                        Logo
                }
            }
        }
    }
}
