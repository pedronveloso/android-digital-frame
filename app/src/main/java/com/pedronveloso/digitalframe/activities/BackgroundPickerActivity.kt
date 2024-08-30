package com.pedronveloso.digitalframe.activities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.exifinterface.media.ExifInterface
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.elements.background.BackgroundAlbumViewModel
import com.pedronveloso.digitalframe.ui.DigitalFrameTheme
import com.pedronveloso.digitalframe.utils.log.LogStoreProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                val coroutineScope = rememberCoroutineScope()

                val progress = remember { mutableStateOf(0) }
                val totalImages = remember { mutableStateOf(0) }
                val isLoading = remember { mutableStateOf(false) }

                ImagePickerLauncher(
                    onImagesPicked = { uris ->
                        totalImages.value = uris.size
                        isLoading.value = true

                        coroutineScope.launch(Dispatchers.IO) {
                            withContext(Dispatchers.Main) {
                            }
                            copyImagesToInternalStorage(
                                context = context,
                                imageUris = uris,
                                directoryName = BackgroundAlbumViewModel.BACKGROUND_PHOTOS_DIR,
                                onProgress = { currentProgress ->
                                        progress.value = currentProgress
                                }
                            )
                            withContext(Dispatchers.Main) {
                                isLoading.value = false
                                finish()
                            }
                        }
                    },
                    isLoading = isLoading.value,
                    progress = progress.value,
                    totalImages = totalImages.value
                )
            }
        }
    }

    @Composable
    fun ImagePickerLauncher(
        onImagesPicked: (List<Uri>) -> Unit,
        isLoading: Boolean,
        progress: Int,
        totalImages: Int
    ) {
        val launcher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents(),
            ) { uris: List<Uri> ->
                onImagesPicked(uris)
            }

        LaunchedEffect(Unit) {
            launcher.launch("image/*")
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(
                            R.string.pref_bg_import_progress,
                            progress,
                            totalImages
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }

    private suspend fun copyImagesToInternalStorage(
        context: Context,
        imageUris: List<Uri>,
        directoryName: String,
        onProgress: (Int) -> Unit
    ) {
        logger.log("Copying ${imageUris.size} image(s) to internal storage")

        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdir()
        }

        val largestDimen = (getLargestScreenDimension(context) * 1.1).toInt()

        imageUris.forEachIndexed { index, uri ->
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val fileName = getFileNameFromUri(context, uri)
                val outputFile = File(directory, "${fileName}.jpg")
                val tempFile = File.createTempFile("temp", null, context.cacheDir)

                logger.log("Copying image to ${outputFile.absolutePath}")

                tempFile.outputStream().use { fileOut ->
                    stream.copyTo(fileOut)
                }

                val resizedBitmap = resizeImage(context, tempFile, largestDimen)

                // Save the resized bitmap with preserved EXIF data
                FileOutputStream(outputFile).use { out ->
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                }

                // Copy EXIF data to the final image
                val originalExif = ExifInterface(tempFile.absolutePath)
                val finalExif = ExifInterface(outputFile.absolutePath)
                copyExifAttributes(originalExif, finalExif)
                finalExif.saveAttributes()

                tempFile.delete()
            }

            withContext(Dispatchers.Main) {
                onProgress(index + 1)
            }
        }
    }

    private fun getLargestScreenDimension(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return max(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    private fun resizeImage(context: Context, file: File, targetSize: Int): Bitmap {
        val originalExif = ExifInterface(file.absolutePath)

        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(file.absolutePath, options)
        val (width, height) = options.run { outWidth to outHeight }
        val scaleFactor = max(width, height).toFloat() / targetSize

        options.inJustDecodeBounds = false
        options.inSampleSize = max(1, scaleFactor.toInt())

        logger.log("Resizing image to $targetSize pixels")

        var resizedBitmap = BitmapFactory.decodeFile(file.absolutePath, options)

        val orientation = originalExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, true)

        val tempFile = File.createTempFile("temp_resized", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }

        val newExif = ExifInterface(tempFile.absolutePath)
        copyExifAttributes(originalExif, newExif)
        newExif.saveAttributes()

        return resizedBitmap
    }

    private fun copyExifAttributes(source: ExifInterface, destination: ExifInterface) {
        val attributes = arrayOf(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.TAG_DATETIME,
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE_REF
        )

        for (attribute in attributes) {
            val value = source.getAttribute(attribute)
            if (value != null) {
                destination.setAttribute(attribute, value)
            }
        }
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String {
        var fileName = "image_${System.currentTimeMillis()}"

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        fileName = fileName.substringBeforeLast('.').substringAfterLast('/')
        fileName = fileName.replace(Regex("[^A-Za-z0-9_]"), "")

        if (fileName.isEmpty()) {
            fileName = "image_${System.currentTimeMillis()}"
        }

        return fileName
    }
}