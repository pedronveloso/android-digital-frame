package com.pedronveloso.digitalframe.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.pedronveloso.digitalframe.elements.PhotosBackgroundViewModel.Companion.BACKGROUND_PHOTOS_DIR
import com.pedronveloso.digitalframe.ui.MyTypography
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

class PreferencesActivity : ComponentActivity() {

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreferencesScreen()
        }

    }

    @Composable
    fun PreferencesScreen() {
        val context = LocalContext.current
        val uris = remember { mutableStateListOf<Uri>() }
        var showImagePicker by remember { mutableStateOf(false) }


        Column {
            PreferenceItem("Pick background photos") {
                showImagePicker = true
            }
            Divider()
            PreferenceItem("Countdown Settings") {
                // TODO: Handle click for "Countdown Settings"
            }
        }
        if (showImagePicker) {
            ImagePickerLauncher { uris ->
                copyImagesToInternalStorage(context, uris, BACKGROUND_PHOTOS_DIR)
                showImagePicker = false
            }
        }
    }

    @Composable
    fun ImagePickerLauncher(onImagesPicked: (List<Uri>) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetMultipleContents()
        ) { uris: List<Uri> ->
            onImagesPicked(uris)
        }

        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
    }
}


@Composable
fun PreferenceItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        style = MyTypography.bodyLarge.copy()
    )
}

fun copyImagesToInternalStorage(context: Context, imageUris: List<Uri>, directoryName: String) {
    val directory = File(context.filesDir, directoryName)
    if (!directory.exists()) {
        directory.mkdir()
    }

    val largestDimen = (getLargestScreenDimension(context) * 1.1).toInt()

    imageUris.forEach { uri ->
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.use { stream ->
            val outputFile = File(directory, uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg")
            val tempFile = File.createTempFile("temp", null, context.cacheDir)

            tempFile.outputStream().use { fileOut ->
                stream.copyTo(fileOut)
            }

            val resizedImage = resizeImage(tempFile, largestDimen)
            val outputStream = FileOutputStream(outputFile)
            resizedImage.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)

            tempFile.delete()
        }
    }
}


fun getLargestScreenDimension(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return max(displayMetrics.widthPixels, displayMetrics.heightPixels)
}

fun resizeImage(file: File, targetSize: Int): Bitmap {
    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(file.absolutePath, options)
    val (width, height) = options.run { outWidth to outHeight }
    val scaleFactor = max(width, height).toFloat() / targetSize

    options.inJustDecodeBounds = false
    options.inSampleSize = max(1, scaleFactor.toInt())

    return BitmapFactory.decodeFile(file.absolutePath, options)
}

