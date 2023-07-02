package com.pedronveloso.digitalframe.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.IntOffset
import com.pedronveloso.digitalframe.ui.Effects.FADING_CADENCE
import com.pedronveloso.digitalframe.ui.Effects.FADING_DURATION
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds


object Effects{
    const val FADING_DURATION = 800
    val FADING_CADENCE = 30.seconds
}

@Composable
fun FadingComposable(content: @Composable () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val alpha = remember { mutableStateOf(1f) }
    val offsetX = remember { mutableStateOf(0f) }
    val offsetY = remember { mutableStateOf(0f) }

    val transition = updateTransition(targetState = alpha.value, label = "transition")
    val animatedAlpha = transition.animateFloat(
        transitionSpec = {
            if (alpha.value == 1f) {
                tween(durationMillis = FADING_DURATION, easing = FastOutSlowInEasing)
            } else {
                tween(durationMillis = FADING_DURATION, easing = FastOutSlowInEasing)
            }
        }, label = ""
    ) { state -> state }

    LaunchedEffect(coroutineScope) {
        while (true) {
            delay(FADING_CADENCE.inWholeMilliseconds)
            alpha.value = 0f
            delay(FADING_DURATION.toLong()) // wait for fade out

            // generate a random position within 20 pixels in either direction
            offsetX.value = Random.nextFloat() * 40 - 20
            offsetY.value = Random.nextFloat() * 40 - 20
            alpha.value = 1f
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
    ) {
        Box(modifier = Modifier.alpha(animatedAlpha.value)) {
            content()
        }
    }
}
