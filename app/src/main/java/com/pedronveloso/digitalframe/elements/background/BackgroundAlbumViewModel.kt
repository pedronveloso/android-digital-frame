package com.pedronveloso.digitalframe.elements.background

import android.content.Context
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.pedronveloso.digitalframe.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

sealed class PhotoResource {
    data class DrawableResource(
        @DrawableRes val id: Int,
    ) : PhotoResource()

    data class FileResource(
        val filePath: String,
    ) : PhotoResource()
}

@HiltViewModel
class BackgroundAlbumViewModel
    @Inject
    constructor(
        private val savedState: SavedStateHandle,
        @ApplicationContext private val appContext: Context,
    ) : ViewModel() {
        companion object {
            val EFFECT_DURATION = 20.seconds
            const val BACKGROUND_PHOTOS_DIR = "background"
        }

        private val _currentPhotoFlow = MutableStateFlow(loadInitialPhoto())
        val currentPhotoFlow: StateFlow<PhotoResource> = _currentPhotoFlow

        private var rotationJob: Job? = null

        private val backgroundHSL = MutableStateFlow(FloatArray(3).apply { this[2] = 0.5f })
        val hsl: StateFlow<FloatArray> = backgroundHSL

        init {
            refreshBackgroundImages()
        }

        private fun refreshBackgroundImages() {
            rotationJob?.cancel()
            rotationJob =
                viewModelScope.launch {
                    backgroundRotation(appContext)
                }
        }

        private suspend fun backgroundRotation(context: Context) {
            val photoList = loadPhotosFromInternalStorage(context)
            while (currentCoroutineContext().isActive) {
                delay(EFFECT_DURATION.inWholeMilliseconds)
                val newPhoto = photoList.random()
                _currentPhotoFlow.emit(newPhoto)

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
                            context
                                .getDrawable(photoResource.id)
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
                    } catch (e: Exception) {
                        // TODO: Handle any errors during brightness calculation
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

        private fun loadDefaultPhotos(): List<PhotoResource> =
            listOf(
                PhotoResource.DrawableResource(R.drawable.photo1),
                PhotoResource.DrawableResource(R.drawable.photo2),
                PhotoResource.DrawableResource(R.drawable.photo3),
            )
    }
