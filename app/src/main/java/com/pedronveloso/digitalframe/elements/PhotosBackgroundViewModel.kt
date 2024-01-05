package com.pedronveloso.digitalframe.elements

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.pedronveloso.digitalframe.R
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

data class PhotoResource(@DrawableRes val id: Int = R.drawable.photo1)

class PhotosBackgroundViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    companion object {
        private val EFFECT_DURATION = 20.seconds
    }

    private var currentPhoto by mutableStateOf(PhotoResource())

    @Composable
    fun RenderBackground() {
        // Will render background image with a Ken Burns effect.
        val infiniteTransition = rememberInfiniteTransition()
        val scale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(EFFECT_DURATION.inWholeMilliseconds.toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offsetX by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 100f,
            animationSpec = infiniteRepeatable(
                animation = tween(EFFECT_DURATION.inWholeMilliseconds.toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offsetY by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 50f,
            animationSpec = infiniteRepeatable(
                animation = tween(EFFECT_DURATION.inWholeMilliseconds.toInt(), easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        LaunchedEffect(key1 = true) {
            while (true) {
                // change image at every EFFECT_DURATION interval.
                delay(EFFECT_DURATION.inWholeMilliseconds)
                val nextPhotoId = listOf(R.drawable.photo1, R.drawable.photo2, R.drawable.photo3, R.drawable.photo4).random()
                currentPhoto = PhotoResource(nextPhotoId)
            }
        }
        Box(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
        ) {
            val imageId = currentPhoto.id
            Image(painter = painterResource(id = imageId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize())
        }
    }
}
