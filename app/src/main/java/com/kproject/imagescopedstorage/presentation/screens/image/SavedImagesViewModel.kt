package com.kproject.imagescopedstorage.presentation.screens.image

import android.app.Application
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kproject.imagescopedstorage.presentation.model.Image
import com.kproject.imagescopedstorage.presentation.utils.Utils
import com.kproject.imagescopedstorage.presentation.utils.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "SavedImagesViewModel"

class SavedImagesViewModel(application: Application) : AndroidViewModel(application) {
    private val context by lazy {
        application.applicationContext
    }

    private val _viewState = mutableStateOf(ViewState.Loading)
    val viewState: State<ViewState> = _viewState

    var savedImagesList = mutableStateListOf<Image>()

    var imageViewerState by mutableStateOf(ImageViewerUiState())
        private set

    fun getSavedImages() {
        _viewState.value = ViewState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val images = mutableListOf<Image>()
            try {
                val collection: Uri = if (Utils.isAndroidQOrAbove()) {
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                } else {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                val projection = arrayOf(
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.DISPLAY_NAME,
                )
                val selection = if (Utils.isAndroidQOrAbove()) {
                    "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
                } else {
                    "${MediaStore.MediaColumns.DATA} LIKE ?"
                }
                val selectionArgs = arrayOf("%${Utils.APP_FOLDER}%")
                val order = "${MediaStore.Images.ImageColumns.DATE_MODIFIED} ASC"
                context.contentResolver.query(
                    collection,
                    projection,
                    selection,
                    selectionArgs,
                    order
                )?.use { cursor ->
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameColumn =
                            cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    while (cursor.moveToNext()) {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )
                        images.add(Image(id, contentUri, displayName))
                    }

                    savedImagesList = images.toMutableStateList()
                    if (savedImagesList.isNotEmpty()) {
                        _viewState.value = ViewState.Success
                    } else {
                        _viewState.value = ViewState.Empty
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error getting images: ${e.message} ${e.stackTrace.joinToString()}"
                )
                _viewState.value = ViewState.Error
            }
        }
    }

    fun deleteImage(
        imageUri: Uri,
        index: Int,
        intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>
    ) {
        viewModelScope.launch {
            try {
                context.contentResolver.delete(imageUri, null, null)
                removeImageFromList(index)
            } catch (e: SecurityException) {
                // This exception will only be thrown from Android 11
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)  {
                    val intentSender = MediaStore.createDeleteRequest(
                        context.contentResolver,
                        listOf(imageUri)
                    ).intentSender

                    intentSenderLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error trying to delete image: ${e.message} ${e.stackTrace.joinToString()}"
                )
            }
        }
    }

    fun removeImageFromList(index: Int) {
        savedImagesList.removeAt(index)
    }

    fun onCurrentPageChange(currentPage: Int) {
        imageViewerState = imageViewerState.copy(currentPage = currentPage)
    }
}