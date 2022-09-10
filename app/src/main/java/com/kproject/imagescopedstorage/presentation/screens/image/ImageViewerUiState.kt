package com.kproject.imagescopedstorage.presentation.screens.image

import android.net.Uri
import com.kproject.imagescopedstorage.presentation.model.Image

data class ImageViewerUiState(
    val currentPage: Int = 0
)

val fakeImageList = (0..20).map { index ->
    Image(
        id = index.toLong(),
        contentUri = Uri.EMPTY,
        displayName = "Image $index"
    )
}