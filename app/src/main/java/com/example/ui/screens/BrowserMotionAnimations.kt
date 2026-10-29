package com.example.ui.screens

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.FrostedGlassBox
import com.example.ui.components.GlassElevation
import com.example.ui.theme.LocalGlassStyle
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val LiquidIceSilver = Color(0xFFF5F9FA)
private val LiquidPrismBlue = Color(0xFF7EC8E3)
private val LiquidPrismCyan = Color(0xFF38BDF8)
private val LiquidPrismSlate = Color(0xFF647D92)
private val LiquidPrismIndigo = Color(0xFF818CF8)

/**
 * 1. BROWSER OPENING ANIMATION:
 * Spatial Liquid-Glass entrance featuring the custom "GL • Glass Browser" brand emblem,
 * dual counter-rotating prismatic halo rings, live specular glass sheen sweep, animated
 * readiness bar, and spatial zoom-through dissolve into the Start Page.
 */
@Composable
fun BrowserOpeningGlassAnimation(
    visible: Boolean,
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val emblemScale = remember { Animatable(0.74f) }
    val emblemAlpha = remember { Animatable(0f) }
    val backdropAlpha = remember { Animatable(1f) }
    val ringSweep = remember { Animatable(0f) }
    val sheenSweep = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Brief window-reveal settle so Android's system SplashScreen finishes crossfading first
        delay(180L)
        coroutineScope {
            launch {
                emblemAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
                )
            }
            launch {
                emblemScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(dampingRatio = 0.62f, stiffness = 260f)
                )
            }
            launch {
                ringSweep.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 1280, easing = FastOutSlowInEasing)
                )
            }
            launch {
                delay(180L)
                sheenSweep.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 980, easing = FastOutSlowInEasing)
                )
            }
        }
        delay(240L)
        coroutineScope {
            launch {
                emblemScale.animateTo(
                    targetValue = 1.15f,
                    animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
                )
            }
            launch {
                emblemAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
                )
            }
            launch {
                backdropAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
                )
            }
        }
        onAnimationFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = backdropAlpha.value }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFAFDFF),
                        LiquidIceSilver,
                        Color(0xFFE3EDF4)
                    )
                )
            )
            .testTag("browser_opening_animation")
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onAnimationFinished
            )
    ) {
        // Ambient Refractive Glass Halo Rings & Counter-Rotating Prismatic Sweeps
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val minDim = size.minDimension
            val progress = ringSweep.value

            val outerRadius = minDim * (0.33f + 0.15f * progress)
            val midRadius = minDim * (0.27f + 0.10f * progress)
            val innerRadius = minDim * (0.21f + 0.06f * progress)

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        LiquidPrismBlue.copy(alpha = 0.28f * (1f - progress * 0.25f)),
                        Color.White.copy(alpha = 0.55f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = outerRadius * 1.35f
                ),
                radius = outerRadius * 1.35f,
                center = Offset(cx, cy)
            )

            rotate(degrees = progress * 180f, pivot = Offset(cx, cy)) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.90f),
                            LiquidPrismBlue.copy(alpha = 0.55f),
                            Color.White.copy(alpha = 0.20f),
                            LiquidPrismCyan.copy(alpha = 0.50f),
                            Color.White.copy(alpha = 0.90f)
                        ),
                        center = Offset(cx, cy)
                    ),
                    radius = outerRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2.4.dp.toPx())
                )
            }

            rotate(degrees = -progress * 135f, pivot = Offset(cx, cy)) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            LiquidPrismIndigo.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.80f),
                            LiquidPrismCyan.copy(alpha = 0.42f),
                            LiquidPrismIndigo.copy(alpha = 0.35f)
                        ),
                        center = Offset(cx, cy)
                    ),
                    radius = midRadius,
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.6.dp.toPx())
                )
            }

            drawCircle(
                color = Color.White.copy(alpha = 0.72f),
                radius = innerRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 1.2.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .graphicsLayer {
                    scaleX = emblemScale.value
                    scaleY = emblemScale.value
                    alpha = emblemAlpha.value
                }
        ) {
            val sheen = sheenSweep.value
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .widthIn(max = 304.dp)
                    .fillMaxWidth()
                    .shadow(
                        elevation = 26.dp,
                        shape = RoundedCornerShape(36.dp),
                        ambientColor = LiquidPrismSlate.copy(alpha = 0.24f),
                        spotColor = LiquidPrismSlate.copy(alpha = 0.30f)
                    )
                    .clip(RoundedCornerShape(36.dp))
                    .background(LiquidIceSilver)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White,
                                LiquidPrismBlue.copy(alpha = 0.50f),
                                Color.White.copy(alpha = 0.90f)
                            )
                        ),
                        shape = RoundedCornerShape(36.dp)
                    )
                    .drawWithContent {
                        drawContent()
                        if (sheen in 0.01f..0.99f) {
                            val bandX = -size.width * 0.4f + size.width * 1.8f * sheen
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.52f),
                                        LiquidPrismCyan.copy(alpha = 0.18f),
                                        Color.Transparent
                                    ),
                                    start = Offset(bandX - size.width * 0.28f, 0f),
                                    end = Offset(bandX + size.width * 0.28f, size.height)
                                )
                            )
                        }
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brand_glass_logo),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(198.dp)
                )
            }

            // Luminous capsule pill + animated liquid readiness bar below emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.78f))
                        .border(
                            width = 1.dp,
                            color = LiquidPrismBlue.copy(alpha = 0.40f),
                            shape = RoundedCornerShape(50)
                        )
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LiquidPrismSpinnerOrb(sizeDp = 16)
                        Text(
                            text = stringResource(R.string.opening_browser_ready_pill),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFF475B6D)
                        )
                    }
                }

                // Smooth filling Liquid-Glass startup bar
                Box(
                    modifier = Modifier
                        .width(148.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(50))
                        .background(LiquidPrismSlate.copy(alpha = 0.14f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(ringSweep.value.coerceIn(0.08f, 1f))
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(LiquidPrismBlue, LiquidPrismCyan, LiquidPrismIndigo)
                                )
                            )
                    )
                }
            }
        }
    }
}

/**
 * 2. SITE OPENING PORTAL TRANSITION CONTAINER:
 * Smoothly animates between the Start Page (`false`) and active Website (`true`) using spatial
 * depth scaling, rounded glass portal framing, and spring physics.
 */
@Composable
fun SitePortalTransitionContainer(
    isViewingWebPage: Boolean,
    modifier: Modifier = Modifier,
    webContent: @Composable () -> Unit,
    startPageContent: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = isViewingWebPage,
        transitionSpec = {
            if (targetState) {
                // Opening a site: incoming webpage springs forward from a spatial portal while Start Page zooms deep
                (
                    fadeIn(animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)) +
                        scaleIn(
                            initialScale = 0.87f,
                            animationSpec = spring(dampingRatio = 0.72f, stiffness = 290f)
                        ) +
                        slideInVertically(
                            initialOffsetY = { fullHeight -> fullHeight / 10 },
                            animationSpec = spring(dampingRatio = 0.74f, stiffness = 300f)
                        )
                    ).togetherWith(
                    fadeOut(animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)) +
                        scaleOut(
                            targetScale = 1.09f,
                            animationSpec = tween(durationMillis = 340, easing = FastOutSlowInEasing)
                        )
                ).using(SizeTransform(clip = false))
            } else {
                // Returning to Start Page: webpage shrinks cleanly back into the Start Page canvas
                (
                    fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)) +
                        scaleIn(
                            initialScale = 0.94f,
                            animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f)
                        )
                    ).togetherWith(
                    fadeOut(animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)) +
                        scaleOut(
                            targetScale = 0.88f,
                            animationSpec = tween(durationMillis = 270, easing = FastOutSlowInEasing)
                        )
                ).using(SizeTransform(clip = false))
            }
        },
        label = "sitePortalTransition",
        modifier = modifier
    ) { viewingSite ->
        if (viewingSite) {
            webContent()
        } else {
            startPageContent()
        }
    }
}

/**
 * Non-blocking Liquid-Glass Portal Ripple Veil that plays for ~880ms whenever a user opens a site
 * (either from the Start Page or jumping to a new domain), adding a vivid refractive lens reveal.
 */
@Composable
fun SiteOpeningGlassVeil(
    siteTransitionNonce: Long,
    siteBrand: String,
    modifier: Modifier = Modifier
) {
    if (siteTransitionNonce <= 0L) return

    var active by remember(siteTransitionNonce) { mutableStateOf(true) }
    val portalProgress = remember(siteTransitionNonce) { Animatable(0f) }

    LaunchedEffect(siteTransitionNonce) {
        active = true
        portalProgress.snapTo(0f)
        portalProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 880, easing = FastOutSlowInEasing)
        )
        active = false
    }

    if (!active) return

    val p = portalProgress.value
    val fadeEnvelope = sin(p * PI).toFloat().coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .testTag("site_opening_animation")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val maxR = size.maxDimension * 0.78f
            val primaryR = maxR * (0.14f + 0.86f * p)
            val secondaryR = maxR * (0.06f + 0.68f * p)
            val corner = (34f * (1f - p)).dp.toPx()

            // Expanding primary prismatic portal wave
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        LiquidPrismCyan.copy(alpha = 0.30f * fadeEnvelope),
                        Color.White.copy(alpha = 0.44f * fadeEnvelope),
                        LiquidPrismIndigo.copy(alpha = 0.20f * fadeEnvelope),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = primaryR.coerceAtLeast(20f)
                ),
                radius = primaryR.coerceAtLeast(20f),
                center = Offset(cx, cy)
            )

            // Crisp concentric inner refractive shockwave ring
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.65f * fadeEnvelope),
                        LiquidPrismCyan.copy(alpha = 0.50f * fadeEnvelope),
                        LiquidPrismIndigo.copy(alpha = 0.45f * fadeEnvelope),
                        Color.White.copy(alpha = 0.65f * fadeEnvelope)
                    ),
                    center = Offset(cx, cy)
                ),
                radius = secondaryR.coerceAtLeast(12f),
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )

            // Morphing perimeter glass frame
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.68f * (1f - p)),
                        LiquidPrismCyan.copy(alpha = 0.52f * (1f - p)),
                        LiquidPrismIndigo.copy(alpha = 0.45f * (1f - p)),
                        Color.White.copy(alpha = 0.62f * (1f - p))
                    )
                ),
                topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                size = Size(
                    (size.width - 8.dp.toPx()).coerceAtLeast(1f),
                    (size.height - 8.dp.toPx()).coerceAtLeast(1f)
                ),
                cornerRadius = CornerRadius(corner, corner),
                style = Stroke(width = 3.dp.toPx())
            )
        }

        if (siteBrand.isNotBlank() && p < 0.90f) {
            FrostedGlassBox(
                shape = RoundedCornerShape(50),
                elevation = GlassElevation.FLOATING_BAR,
                useLiveSceneBackdrop = true,
                modifier = Modifier.graphicsLayer {
                    val pillScale = if (p < 0.25f) {
                        0.82f + (p / 0.25f) * 0.18f
                    } else {
                        1.0f + ((p - 0.25f) / 0.65f) * 0.08f
                    }
                    scaleX = pillScale
                    scaleY = pillScale
                    translationY = -28f * p
                    alpha = if (p < 0.18f) {
                        (p / 0.18f).coerceIn(0f, 1f)
                    } else {
                        (1f - ((p - 0.18f) / 0.72f)).coerceIn(0f, 1f)
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    LiquidPrismSpinnerOrb(sizeDp = 18)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(LiquidPrismCyan, LiquidPrismIndigo)
                                )
                            )
                    ) {
                        Text(
                            text = siteBrand.take(1).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                    Text(
                        text = stringResource(R.string.opening_site_format, siteBrand),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = LocalGlassStyle.current.contentPrimary
                    )
                }
            }
        }
    }
}

/**
 * 3. WEBPAGE LOADING ANIMATION:
 * Dual-layer Liquid-Glass loading feedback while a webpage loads, with guaranteed smooth
 * 100% completion sweep so fast/cached pages still show fluid visual confirmation:
 *  - Luminous animated multi-stop liquid-wave ribbon along the top edge (`web_loading_ribbon`).
 *  - Compact floating frosted-glass pill with dual counter-rotating prism rings, site title, and
 *    live animated progress percentage (`web_loading_animation`), passing all touches cleanly through.
 */
@Composable
fun WebPageGlassLoadingAnimation(
    visible: Boolean,
    progress: Float,
    siteTitleOrDomain: String,
    modifier: Modifier = Modifier
) {
    val style = LocalGlassStyle.current
    var displayVisible by remember { mutableStateOf(visible) }
    var shownAtUptimeMs by remember { mutableLongStateOf(0L) }

    LaunchedEffect(visible) {
        if (visible) {
            if (!displayVisible) {
                shownAtUptimeMs = SystemClock.uptimeMillis()
            }
            displayVisible = true
        } else if (displayVisible) {
            val elapsed = SystemClock.uptimeMillis() - shownAtUptimeMs
            val remainingDwell = (720L - elapsed).coerceAtLeast(260L)
            delay(remainingDwell)
            displayVisible = false
        }
    }

    val clampedTarget = if (visible) progress.coerceIn(0.10f, 0.96f) else 1f
    val smoothProgress by animateFloatAsState(
        targetValue = clampedTarget,
        animationSpec = spring(dampingRatio = 0.80f, stiffness = 240f),
        label = "smoothWebLoadProgress"
    )

    AnimatedVisibility(
        visible = displayVisible,
        enter = fadeIn(tween(180)) + scaleIn(
            initialScale = 0.93f,
            animationSpec = spring(dampingRatio = 0.72f, stiffness = 360f)
        ),
        exit = fadeOut(tween(260)) + scaleOut(targetScale = 0.95f, animationSpec = tween(240)),
        modifier = modifier.fillMaxWidth()
    ) {
        val infinite = rememberInfiniteTransition(label = "webGlassLoading")
        val wavePhase by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1050, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "loadingWavePhase"
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Top Luminous Liquid-Glass Wave Ribbon
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.5.dp)
                    .testTag("web_loading_ribbon")
            ) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas

                // Subtle frosted track
                drawRect(
                    color = Color.White.copy(alpha = 0.18f),
                    size = Size(w, h)
                )

                val activeW = (w * smoothProgress).coerceIn(28.dp.toPx(), w)
                val shimmerCenter = (activeW * wavePhase).coerceIn(0f, activeW)

                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            LiquidPrismBlue,
                            LiquidPrismCyan,
                            Color.White,
                            LiquidPrismIndigo,
                            LiquidPrismCyan
                        ),
                        startX = shimmerCenter - activeW * 0.55f,
                        endX = shimmerCenter + activeW * 0.55f
                    ),
                    topLeft = Offset.Zero,
                    size = Size(activeW, h),
                    cornerRadius = CornerRadius(h / 2f, h / 2f)
                )

                // Specular comet tip glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.96f),
                            LiquidPrismCyan.copy(alpha = 0.62f),
                            Color.Transparent
                        ),
                        center = Offset(activeW, h / 2f),
                        radius = 12.dp.toPx()
                    ),
                    radius = 12.dp.toPx(),
                    center = Offset(activeW, h / 2f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compact Floating Liquid-Glass Orbital Loading Pill
            FrostedGlassBox(
                shape = RoundedCornerShape(50),
                elevation = GlassElevation.FLOATING_BAR,
                useLiveSceneBackdrop = true,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .testTag("web_loading_animation")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    LiquidPrismSpinnerOrb(sizeDp = 18)

                    val labelText = if (siteTitleOrDomain.isNotBlank()) {
                        stringResource(R.string.loading_site_format, siteTitleOrDomain)
                    } else {
                        stringResource(R.string.loading_web_fallback)
                    }
                    Text(
                        text = labelText,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = style.contentPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 190.dp)
                    )

                    val pct = (smoothProgress * 100f).roundToInt().coerceIn(8, 100)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(style.activeSegmentFill)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$pct%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = style.contentSecondary
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dual-ring counter-rotating Liquid-Glass prism spinner with pulsing luminous core droplet.
 */
@Composable
fun LiquidPrismSpinnerOrb(
    sizeDp: Int = 18,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "liquidPrismSpinner")
    val outerAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1050, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerSpin"
    )
    val corePulse by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 680, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    Canvas(modifier = modifier.size(sizeDp.dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val r = size.minDimension / 2f
        val strokeOuter = 2.dp.toPx()
        val strokeInner = 1.5.dp.toPx()

        rotate(degrees = outerAngle, pivot = center) {
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color.Transparent,
                        LiquidPrismBlue,
                        LiquidPrismCyan,
                        Color.White
                    ),
                    center = center
                ),
                startAngle = 15f,
                sweepAngle = 265f,
                useCenter = false,
                topLeft = Offset(strokeOuter, strokeOuter),
                size = Size(
                    (size.width - strokeOuter * 2f).coerceAtLeast(2f),
                    (size.height - strokeOuter * 2f).coerceAtLeast(2f)
                ),
                style = Stroke(width = strokeOuter, cap = StrokeCap.Round)
            )
        }

        rotate(degrees = -outerAngle * 1.35f, pivot = center) {
            val inset = r * 0.34f
            drawArc(
                color = LiquidPrismIndigo.copy(alpha = 0.85f),
                startAngle = 120f,
                sweepAngle = 190f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(
                    (size.width - inset * 2f).coerceAtLeast(2f),
                    (size.height - inset * 2f).coerceAtLeast(2f)
                ),
                style = Stroke(width = strokeInner, cap = StrokeCap.Round)
            )
        }

        drawCircle(
            color = Color.White.copy(alpha = 0.90f),
            radius = (r * 0.20f * corePulse).coerceAtLeast(1f),
            center = center
        )
    }
}
