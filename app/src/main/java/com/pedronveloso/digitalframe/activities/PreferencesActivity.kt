package com.pedronveloso.digitalframe.activities

import android.content.Context
import android.content.Intent
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
import com.pedronveloso.digitalframe.ui.MyTypography
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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
                copyImagesToInternalStorage(context, uris, "background")
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

    imageUris.forEach { uri ->
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.let { stream ->
            val outputFile = File(directory, uri.lastPathSegment ?: "image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(outputFile)

            val buffer = ByteArray(1024)
            var length: Int
            while (stream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }

            outputStream.close()
            stream.close()
        }
    }
}