package com.flipzon.app.core.Animationcomponet


import androidx.compose.animation.core.*
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────
//  DynamicIslandScope — close() method content ke andar milega
// ─────────────────────────────────────────────────────────────
class DynamicIslandScopePoutry(
    val progress: Float,         // 0f = collapsed, 1f = fully expanded
    private val onClose: () -> Unit
) {
    fun close() = onClose()      // call this from inside content to snap back
}

// ─────────────────────────────────────────────────────────────
//  DynamicIsland — THE REUSABLE ANIMATION WRAPPER
//
//  Usage:
//
//  DynamicIsland(
//      collapsedContent = { Text("Void Finance", color = Color.White) },
//      expandedContent  = { scope ->
//          // yahan kuch bhi daal do
//          Button(onClick = { scope.close() }) { Text("Close") }
//      }
//  )
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DynamicIsland(
    modifier: Modifier = Modifier,

    // ── Geometry ──────────────────────────────────────────────
    topPadding: Dp = 48.dp,          // status bar height
    collapsedWidth: Dp = 220.dp,
    collapsedHeight: Dp = 56.dp,
    collapsedCorner: Dp = 28.dp,
    expandedCorner: Dp = 42.dp,
    horizontalMargin: Dp = 16.dp,
    bottomMargin: Dp = 96.dp,

    // ── Visuals ───────────────────────────────────────────────
    backgroundBrush: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFFB2EBF2),  // Light Ice Blue
            Color(0xFF80DEEA),  // Aqua Frost
            Color(0xFFE0F7FA)   // Icy White
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)
    ),
    borderBrush: Brush = Brush.sweepGradient(
        listOf(
            Color(0xFFFFFFFF),  // Pure Shine
            Color(0xFFB3E5FC),  // Ice Blue
            Color(0xFF81D4FA),  // Sky Ice
            Color(0xFF4FC3F7),  // Cold Aqua
            Color(0xFFFFFFFF)   // Shine Loop
        )
    ),

    // ── Content slots ─────────────────────────────────────────
    collapsedContent: @Composable BoxScope.() -> Unit,
    expandedContent: @Composable BoxScope.(scope: DynamicIslandScopePoutry) -> Unit,

    // ── Callbacks ─────────────────────────────────────────────
    onProgressChange: (Float) -> Unit = {}
) {
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val screenWidth = config.screenWidthDp.dp
    val scope = rememberCoroutineScope()

    // ── Size math ─────────────────────────────────────────────
    val expandedHeight = config.screenHeightDp.dp - bottomMargin
    val expandedWidth = screenWidth - (horizontalMargin * 2)

    val startPx = with(density) { collapsedHeight.toPx() }
    val endPx = with(density) { expandedHeight.toPx() }

    // ── Anchors ───────────────────────────────────────────────
    val anchors = DraggableAnchors {
        BlobState.Collapsed at startPx
        BlobState.Expanded at endPx
    }

    val decaySpec = rememberSplineBasedDecay<Float>()
    val dragState = remember {
        AnchoredDraggableState(
            initialValue = BlobState.Collapsed,
            anchors = anchors,
            positionalThreshold = { it * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = spring(dampingRatio = 0.85f, stiffness = 250f),
            decayAnimationSpec = decaySpec
        )
    }

    // ── Progress ──────────────────────────────────────────────
    val currentHeightPx = if (dragState.offset.isNaN()) startPx else dragState.offset
    val rawProgress = ((currentHeightPx - startPx) / (endPx - startPx)).coerceIn(0f, 1f)
    val smoothProgress by animateFloatAsState(
        targetValue = rawProgress,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = 300f),
        label = "IslandProgress"
    )

    LaunchedEffect(smoothProgress) { onProgressChange(smoothProgress) }

    // ── Width / corner easing ─────────────────────────────────
    val ease = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    val easedProgress = ease.transform(smoothProgress)
    val currentWidth = lerp(collapsedWidth, expandedWidth, easedProgress)
    val currentCorner = lerp(collapsedCorner, expandedCorner, easedProgress)

    // ── The island box ────────────────────────────────────────
    Box(
        modifier = modifier
            .padding(top = topPadding)
            .offset {
                IntOffset(
                    x = ((screenWidth.toPx() - currentWidth.toPx()) / 2).roundToInt(),
                    y = 0
                )
            }
            .width(currentWidth)
            .height(with(density) { currentHeightPx.toDp() })
            .graphicsLayer {
                shape = RoundedCornerShape(currentCorner)
                clip = true
                shadowElevation = smoothProgress * 40f
                spotShadowColor = Color(0xFF4F46E5).copy(alpha = 0.8f)
                ambientShadowColor = Color(0xFFC084FC).copy(alpha = 0.8f)
            }
            .background(backgroundBrush)
            .border(1.dp, borderBrush, RoundedCornerShape(currentCorner))
            .anchoredDraggable(state = dragState, orientation = Orientation.Vertical)
    ) {
        // collapsed content — fades out as island opens
        val collapsedAlpha = (1f - smoothProgress * 4f).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = collapsedAlpha },
            contentAlignment = Alignment.Center,
            content = collapsedContent
        )

        // expanded content — fades in after ~40% open
        val expandedAlpha = ((smoothProgress - 0.35f) * 3f).coerceIn(0f, 1f)
        val islandScope = DynamicIslandScopePoutry(
            progress = smoothProgress,
            onClose = { scope.launch { dragState.animateTo(BlobState.Collapsed) } }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = expandedAlpha },
            contentAlignment = Alignment.TopStart,
        ) {
            expandedContent(islandScope)
        }
    }
}
