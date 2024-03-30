package com.pedronveloso.digitalframe.elements.background

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun AlbumBackground(viewModel: BackgroundAlbumViewModel) {
    val imageResource by viewModel.currentPhotoFlow.collectAsState()

    val painter: Painter =
        when (imageResource) {
            is PhotoResource.DrawableResource -> rememberAsyncImagePainter(model = (imageResource as PhotoResource.DrawableResource).id)
            is PhotoResource.FileResource -> rememberAsyncImagePainter(model = File((imageResource as PhotoResource.FileResource).filePath))
        }

    // Will render background image with a Ken Burns effect.
    val infiniteTransition = rememberInfiniteTransition(label = "ken-burns-bg")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec =
        infiniteRepeatable(
            animation =
            tween(
                BackgroundAlbumViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ken-burns-bg-scale",
    )
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec =
        infiniteRepeatable(
            animation =
            tween(
                BackgroundAlbumViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ken-burns-bg-offset-x",
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 50f,
        animationSpec =
        infiniteRepeatable(
            animation =
            tween(
                BackgroundAlbumViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "ken-burns-bg-offset-y",
    )
    Box(
        modifier =
        Modifier
            .fillMaxSize()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale,
                translationX = offsetX,
                translationY = offsetY,
            ),
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
    }
}
