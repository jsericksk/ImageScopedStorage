package com.kproject.imagescopedstorage.presentation.screens.home

import android.app.Application
import android.content.ContentValues
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kproject.imagescopedstorage.presentation.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

private const val TAG = "HomeViewModel"

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val context by lazy {
        application.applicationContext
    }

    fun saveImage(
        bitmap: Bitmap,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Used to scan the file on Android devices < 10
                var imageFile: File? = null
                val imageName = "${Utils.getCurrentDate()}.png"
                val folderToSave = Utils.APP_FOLDER
                val imageOutputStream: OutputStream?
                if (Utils.isAndroidQOrAbove()) {
                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, folderToSave)
                    val uri: Uri? =
                            context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values
                            )
                    imageOutputStream = context.contentResolver.openOutputStream(uri!!)
                } else {
                    val imagePath =
                            Environment.getExternalStoragePublicDirectory(folderToSave).toString()
                    File(imagePath).mkdirs()
                    imageFile = File(imagePath, imageName)
                    imageOutputStream = FileOutputStream(imageFile)
                }

                imageOutputStream.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    viewModelScope.launch(Dispatchers.Main) { onSuccess.invoke() }

                    /**
                     * Scans the file so it can appear in the gallery right after insertion.
                     * This is useful for some phones that don't automatically scan the gallery.
                     */
                    imageFile?.let {
                        MediaScannerConnection.scanFile(
                            context,
                            arrayOf(it.toString()),
                            null
                        ) { path, uri -> }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving image: ${e.message}\n${e.stackTrace.joinToString()}")
                viewModelScope.launch(Dispatchers.Main) { onError.invoke() }
            }
        }
    }
}
