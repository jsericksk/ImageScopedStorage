package com.kproject.imagescopedstorage.models

import android.net.Uri

data class Image(
    val id: Long,
    val contentUri: Uri,
    val displayName: String
)