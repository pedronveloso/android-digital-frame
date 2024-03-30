package com.pedronveloso.digitalframe.elements.background

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.pedronveloso.digitalframe.R
import com.pedronveloso.digitalframe.elements.background.PhotosBackgroundViewModel.Companion.BACKGROUND_PHOTOS_DIR
import java.io.File


class BackgroundPhotosEraser(private val context: Context) {

    /**
     * Function to show the confirmation dialog and delete photos.
     */
    fun showDeletePhotosConfirmationDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.pref_bg_erase_all)
        builder.setMessage(R.string.pref_bg_erase_all_warning)

        builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
            dialog.dismiss()
            deleteBackgroundPhotosDirectory()
        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteBackgroundPhotosDirectory() {
        val backgroundPhotosDir = File(context.filesDir, BACKGROUND_PHOTOS_DIR)
        if (backgroundPhotosDir.exists()) {
            backgroundPhotosDir.deleteRecursively()
            Toast.makeText(context, R.string.pref_bg_erase_success, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, R.string.pref_bg_erase_failed_empty, Toast.LENGTH_SHORT).show()
        }
    }
}

