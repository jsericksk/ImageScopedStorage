package com.kproject.imagescopedstorage.iu.activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kproject.imagescopedstorage.databinding.ActivityMainBinding
import com.kproject.imagescopedstorage.utils.Utils
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var readPermissionGranted = false
    private var writePermissionGranted = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionsLauncher =
                registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                    readPermissionGranted = permissions[Manifest.permission.READ_EXTERNAL_STORAGE]
                        ?: readPermissionGranted
                    writePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE]
                        ?: writePermissionGranted

                    if (!readPermissionGranted) {
                        showDialogExplainingPermissions()
                    }
                }
        updateOrRequestPermissions()

        with(binding) {
            btSave.setOnClickListener {
                lifecycleScope.launch {
                    saveImage(
                        "image ${System.currentTimeMillis()}.png",
                        Utils.convertViewToBitmap(llLayout)
                    )
                }
            }

            btShowImageList.setOnClickListener {
                startActivity(Intent(this@MainActivity, ImageListActivity::class.java))
            }
        }
    }

    private fun updateOrRequestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private suspend fun saveImage(imageName: String, bitmap: Bitmap) {
        withContext(Dispatchers.IO) {
            val folderToSave = Utils.APP_FOLDER
            val imageOutputStream: OutputStream?
            if (Utils.isSdk29OrAbove()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                values.put(MediaStore.Images.Media.RELATIVE_PATH, folderToSave)
                val uri: Uri? =
                        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                imageOutputStream = contentResolver.openOutputStream(uri!!)
            } else {
                /**
                 * getExternalStoragePublicDirectory() está obsoleto a partir da API 29 (Android Q),
                 * mas como está sendo chamado somente em versões anteriores ao Android 10, não há
                 * problema.
                 */
                val imagePath =
                        Environment.getExternalStoragePublicDirectory(folderToSave).toString()
                val imageToSave = File(imagePath, imageName)
                if (!File(imagePath).exists()) {
                    File(imagePath).mkdirs()
                }
                imageOutputStream = FileOutputStream(imageToSave)
            }

            imageOutputStream.use { outputStream ->
                if (!bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw IOException("Unable to save image.")
                }
            }
        }
    }

    /**
     * Mensagem de informação do uso das permissões para caso o usuário já tenha negado
     * as permissões pelo menos uma vez.
     */
    private fun showDialogExplainingPermissions() {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Permission Error")
        dialog.setMessage("It is necessary to activate the permissions so that the app can get and save images.")
        dialog.setPositiveButton("Ok") { dialogInterface, pos ->
            updateOrRequestPermissions()
            dialogInterface.dismiss()
        }
        dialog.setCancelable(false)
        dialog.show()
    }
}