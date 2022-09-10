package com.kproject.imagescopedstorage.presentation.screens.image

import android.net.Uri
import com.kproject.imagescopedstorage.presentation.model.Image

data class ImageViewerUiState(
    val currentPage: Int = 0
)

val fakeImageList = listOf(
    Image(
        id = 0,
        contentUri = Uri.EMPTY,
        displayName = "Image 1"
    ),
    Image(
        id = 1,
        contentUri = Uri.EMPTY,
        displayName = "Image 2"
    ),
    Image(
        id = 3,
        contentUri = Uri.EMPTY,
        displayName = "Image 3"
    )
)