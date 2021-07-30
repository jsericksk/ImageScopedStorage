package com.kproject.imagescopedstorage.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.os.Environment
import android.view.View
import android.widget.Toast
import java.io.File

object Utils {
    val APP_FOLDER: String =
            "${Environment.DIRECTORY_PICTURES}${File.separator}My App"

    fun isSdk29OrAbove(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun convertViewToBitmap(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }
}