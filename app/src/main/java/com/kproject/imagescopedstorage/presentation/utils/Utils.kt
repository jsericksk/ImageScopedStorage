package com.kproject.imagescopedstorage.presentation.utils

import android.os.Build
import android.os.Environment
import java.io.File

object Utils {
    val APP_FOLDER: String =
            "${Environment.DIRECTORY_PICTURES}${File.separator}Image Scoped Storage"

    fun isAndroidQOrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}