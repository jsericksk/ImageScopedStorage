package com.kproject.imagescopedstorage.presentation.model

import android.net.Uri

data class Image(
    val id: Long,
    val contentUri: Uri,
    val displayName: String
)

val fakeImageList = (0..20).map { index ->
    Image(
        id = index.toLong(),
        contentUri = Uri.EMPTY,
        displayName = "Image $index"
    )
}