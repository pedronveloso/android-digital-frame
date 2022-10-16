package com.pedronveloso.digitalframe.elements

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pedronveloso.digitalframe.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PhotoResource(@DrawableRes val id: Int = R.drawable.photo1)

class PhotosBackgroundViewModel(
    private val savedState: SavedStateHandle
) : ViewModel() {

    private var currentPhoto by mutableStateOf(PhotoResource())

    init {
        repeatedExecution()
    }

    private fun repeatedExecution() {
        viewModelScope.launch {
            delay(20000)
            val nextPhotoId = listOf(R.drawable.photo1, R.drawable.photo2, R.drawable.photo3, R.drawable.photo4).random()
            currentPhoto = PhotoResource(nextPhotoId)
            repeatedExecution()
        }
    }

    @Composable
    fun RenderBackground() {
        val imageId = currentPhoto.id
        Image(painter = painterResource(id = imageId), contentDescription = null, contentScale = ContentScale.Crop)
    }
}
