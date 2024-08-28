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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.exifinterface.media.ExifInterface
import com.pedronveloso.digitalframe.elements.background.BackgroundAlbumViewModel
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
                        BackgroundAlbumViewModel.BACKGROUND_PHOTOS_DIR,
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

    private fun copyImagesToInternalStorage(context: Context, imageUris: List<Uri>, directoryName: String) {
        logger.log("Copying ${imageUris.size} image(s) to internal storage")

        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdir()
        }

        val largestDimen = (getLargestScreenDimension(context) * 1.1).toInt()

        imageUris.forEach { uri ->
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
        }
    }

    private fun getLargestScreenDimension(context: Context): Int {
        val displayMetrics = context.resources.displayMetrics
        return max(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    private fun resizeImage(context: Context, file: File, targetSize: Int): Bitmap {
        // Read the EXIF data from the original file
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

        // Handle rotation based on EXIF data
        val orientation = originalExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
        }
        resizedBitmap = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.width, resizedBitmap.height, matrix, true)

        // Create a temporary file to store the resized image with EXIF data
        val tempFile = File.createTempFile("temp_resized", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }

        // Copy the original EXIF data to the new file
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

        // Try to fetch the display name from the content resolver
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                fileName = cursor.getString(nameIndex)
            }
        }

        // Remove the file extension and any preceding path
        fileName = fileName.substringBeforeLast('.').substringAfterLast('/')

        // Remove any non-alphanumeric characters except underscore
        fileName = fileName.replace(Regex("[^A-Za-z0-9_]"), "")

        // Ensure the filename is not empty
        if (fileName.isEmpty()) {
            fileName = "image_${System.currentTimeMillis()}"
        }

        return fileName
    }

}
