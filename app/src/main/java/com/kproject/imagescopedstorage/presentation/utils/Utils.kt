package com.kproject.imagescopedstorage.presentation.utils

import android.os.Build
import android.os.Environment
import java.io.File

object Utils {
    val APP_FOLDER: String =
            "${Environment.DIRECTORY_PICTURES}${File.separator}Image Scoped Storage"

    fun isAndroidQOrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun permissionList(): List<String> {
        val readPermission = if (Build.VERSION.SDK_INT >= 33) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            listOf(readPermission)
        } else {
            listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}