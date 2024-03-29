package com.kproject.imagescopedstorage.presentation.utils

import android.content.Context
import android.os.Build
import android.os.Environment
import android.widget.Toast
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val APP_FOLDER: String =
            "${Environment.DIRECTORY_PICTURES}${File.separator}Image Scoped Storage"

    fun isAndroidQOrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        Toast.makeText(context, message, duration).show()
    }

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

    fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy_MM_dd-HH_mm_ss", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}