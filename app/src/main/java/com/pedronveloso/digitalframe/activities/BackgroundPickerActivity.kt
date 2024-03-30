package com.pedronveloso.digitalframe.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.pedronveloso.digitalframe.elements.background.PhotosBackgroundViewModel
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max

class BackgroundPickerActivity : ComponentActivity() {

    private val logger = LogStoreProvider.getLogStore()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        logger.log("Creating BackgroundPicker Screen")

        setContent {
            DigitalFrameTheme {
                val context = LocalContext.current

                ImagePickerLauncher { uris ->
                    copyImagesToInternalStorage(
                        context,
                        uris,
                        PhotosBackgroundViewModel.BACKGROUND_PHOTOS_DIR,
                    )

                    finish()
                }
            }
        }
    }

    @Composable
    fun ImagePickerLauncher(onImagesPicked: (List<Uri>) -> Unit) {
        val launcher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents(),
            ) { uris: List<Uri> ->
                onImagesPicked(uris)
            }

        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }
    }

    private fun copyImagesToInternalStorage(
        context: Context,
        imageUris: List<Uri>,
        directoryName: String,
    ) {
        logger.log("Copying ${imageUris.size} image(s) to internal storage")

        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdir()
        }

        val largestDimen = (getLargestScreenDimension(context) * 1.1).toInt()

        imageUris.forEach { uri ->
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val outputFile =
                    File(directory, uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg")
                val tempFile = File.createTempFile("temp", null, context.cacheDir)

                logger.log("Copying image to ${outputFile.absolutePath}")

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

    private fun getLargestScreenDimension(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return max(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    private fun resizeImage(
        file: File,
        targetSize: Int,
    ): Bitmap {
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
        BitmapFactory.decodeFile(file.absolutePath, options)
        val (width, height) = options.run { outWidth to outHeight }
        val scaleFactor = max(width, height).toFloat() / targetSize

        options.inJustDecodeBounds = false
        options.inSampleSize = max(1, scaleFactor.toInt())

        logger.log("Resizing image to $targetSize pixels")

        return BitmapFactory.decodeFile(file.absolutePath, options)
    }

}



