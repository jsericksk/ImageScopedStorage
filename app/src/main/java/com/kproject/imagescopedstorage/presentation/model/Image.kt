package com.kproject.imagescopedstorage.presentation.model

import android.net.Uri

data class Image(
    val id: Long,
    val contentUri: Uri,
    val displayName: String
)