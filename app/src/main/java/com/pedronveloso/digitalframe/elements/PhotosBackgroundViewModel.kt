package com.pedronveloso.digitalframe.elements

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import coil.compose.rememberAsyncImagePainter
import com.pedronveloso.digitalframe.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed class PhotoResource {
    data class DrawableResource(
        @DrawableRes val id: Int,
    ) : PhotoResource()

    data class FileResource(val filePath: String) : PhotoResource()
}

@HiltViewModel
class PhotosBackgroundViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
        @ApplicationContext private val appContext: Context,
    ) : ViewModel() {
        companion object {
            val EFFECT_DURATION = 5.seconds
            const val BACKGROUND_PHOTOS_DIR = "background"
        }

        private val photoFlow = MutableStateFlow(loadInitialPhoto())

        val currentPhoto: StateFlow<PhotoResource> = photoFlow

        private val backgroundHSL = MutableStateFlow(FloatArray(3).apply { this[2] = 0.5f })
        val hsl: StateFlow<FloatArray> = backgroundHSL

        init {
            viewModelScope.launch {
                backgroundRotation(appContext)
            }
        }

        private suspend fun backgroundRotation(context: Context) {
            val photoList = loadPhotosFromInternalStorage(context)
            while (isActive) {
                delay(EFFECT_DURATION.inWholeMilliseconds)
                val newPhoto = photoList.random()
                photoFlow.emit(newPhoto)

                // Calculate and emit new brightness value.
                calculateBrightness(newPhoto, context)
            }
        }

        private fun calculateBrightness(
            photoResource: PhotoResource,
            context: Context,
        ) {
            viewModelScope.launch {
                val bitmap =
                    when (photoResource) {
                        is PhotoResource.DrawableResource ->
                            context.getDrawable(photoResource.id)
                                ?.toBitmap()

                        is PhotoResource.FileResource -> BitmapFactory.decodeFile(photoResource.filePath)
                        else -> null
                    }
                if (bitmap != null) {
                    try {
                        val palette = Palette.from(bitmap).generate()
                        val dominantSwatch = palette.dominantSwatch
                        val hslValues = dominantSwatch?.hsl ?: FloatArray(3).apply { this[2] = 0f }
                        backgroundHSL.emit(hslValues)
                        Log.v("PhotosBackgroundViewModel", "HSL: ${hslValues.contentToString()}")
                    } catch (e: Exception) {
                        // Handle any errors during brightness calculation
                        Log.e("PhotosBackgroundViewModel", "Error calculating HSL", e)
                    }
                }
            }
        }

        private fun loadInitialPhoto(): PhotoResource {
            // Assuming a context is available here; if not, you need to pass it
            val context: Context = appContext
            val photosFromStorage = loadPhotosFromInternalStorage(context)
            return if (photosFromStorage.isNotEmpty()) {
                photosFromStorage.random()
            } else {
                PhotoResource.DrawableResource(R.drawable.photo1)
            }
        }

        private fun loadPhotosFromInternalStorage(context: Context): List<PhotoResource> {
            val backgroundDir = File(context.filesDir, BACKGROUND_PHOTOS_DIR)
            val images = mutableListOf<PhotoResource>()

            if (backgroundDir.exists() && backgroundDir.isDirectory) {
                backgroundDir.listFiles { file -> file.isFile && file.canRead() }?.forEach { file ->
                    images.add(PhotoResource.FileResource(file.absolutePath))
                }
            }

            return images.ifEmpty { loadDefaultPhotos() }
        }

        private fun loadDefaultPhotos(): List<PhotoResource> {
            return listOf(
                PhotoResource.DrawableResource(R.drawable.photo1),
                PhotoResource.DrawableResource(R.drawable.photo2),
                PhotoResource.DrawableResource(R.drawable.photo3),
                PhotoResource.DrawableResource(R.drawable.photo1),
                PhotoResource.DrawableResource(R.drawable.photo4),
            )
        }
    }

@Composable
fun RenderBackground(viewModel: PhotosBackgroundViewModel) {
    val imageResource by viewModel.currentPhoto.collectAsState()

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
                        PhotosBackgroundViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
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
                        PhotosBackgroundViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
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
                        PhotosBackgroundViewModel.EFFECT_DURATION.inWholeMilliseconds.toInt(),
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
