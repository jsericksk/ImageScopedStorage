package com.kproject.imagescopedstorage.iu.activities

import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.databinding.ActivityImageListBinding
import com.kproject.imagescopedstorage.iu.adapters.ImageListAdapter
import com.kproject.imagescopedstorage.models.Image
import com.kproject.imagescopedstorage.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageListBinding
    private lateinit var imageListAdapter: ImageListAdapter

    private lateinit var intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest>

    private var deletedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            with (binding.rvImageList) {
                layoutManager = GridLayoutManager(this@ImageListActivity, 3)
                imageListAdapter = ImageListAdapter(loadAppImages()) { image ->
                    showDialogDeleteImage(image.contentUri)
                }
                adapter = imageListAdapter
                supportActionBar?.subtitle = "App Images"
            }
        }

        intentSenderLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            // RESULT_OK ser?? obtido caso o usu??rio permita a exclus??o da imagem
            if (it.resultCode == RESULT_OK) {
                /**
                 * Tratamento especial para o Android 10, pois curiosamente a imagem n??o ser?? exclu??da
                 * mesmo ap??s a permiss??o do usu??rio. deleteImage() ser?? chamado novamente, mas como
                 * agora a permiss??o do usu??rio j?? foi concedida, uma SecurityException n??o ser?? lan??ada
                 * e contentResolver.delete() far?? o trabalho. No Android 11 esse problema n??o acontece.
                 */
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
                    lifecycleScope.launch {
                        deleteImage(deletedImageUri ?: return@launch)
                        reloadImageList()
                    }
                } else {
                    Utils.showToast(this@ImageListActivity, "Image successfully deleted!")
                    reloadImageList()
                }
            } else {
                Utils.showToast(this@ImageListActivity, "There was an error deleting image.")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_load_all_images -> {
                lifecycleScope.launch {
                    imageListAdapter.updateImageList(loadAllImages())
                    supportActionBar?.subtitle = "All Images"
                }
            }
            R.id.menu_load_app_images -> {
                lifecycleScope.launch {
                    imageListAdapter.updateImageList(loadAppImages())
                    supportActionBar?.subtitle = "App Images"
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private  suspend fun loadAllImages(): List<Image> {
        val images = mutableListOf<Image>()
        withContext(Dispatchers.IO) {
            val collection: Uri = if (Utils.isSdk29OrAbove()) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val order = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            contentResolver.query(
                collection,
                projection,
                null,
                null,
                order
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    images.add(Image(id, contentUri, displayName))
                }
                images.toList()
            } ?: listOf()
        }
        return images
    }

    private suspend fun loadAppImages(): List<Image> {
        val images = mutableListOf<Image>()
        withContext(Dispatchers.IO) {
            val collection: Uri = if (Utils.isSdk29OrAbove()) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val selection = if (Utils.isSdk29OrAbove()) {
                "${MediaStore.Images.Media.RELATIVE_PATH} LIKE ?"
            } else {
                "${MediaStore.MediaColumns.DATA} LIKE ?"
            }
            val selectionArgs = arrayOf("%${Utils.APP_FOLDER}%")
            val order = "${MediaStore.Images.Media.DISPLAY_NAME} ASC"
            contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                order
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    images.add(Image(id, contentUri, displayName))
                }
                images.toList()
            } ?: listOf()
        }
        return images
    }

    private suspend fun deleteImage(imageUri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                /**
                 * Isso funcionar?? bem nas APIs < 29, mas a partir da API 29 (Android Q),
                 * s?? funcionar?? caso a imagem em quest??o tenha sido criada pelo pr??prio app.
                 * Se a imagem foi criada por outro app (como a c??mera), ent??o uma SecurityException
                 * ser?? lan??ada e o usu??rio ter?? que confirmar manualmente a exclus??o da imagem
                 * atrav??s de uma caixa de di??logo padr??o do sistema.
                 * Se o usu??rio excluir os dados do app ou desinstalar e instalar novamente, as imagens
                 * anteriormentes criadas pelo app ser??o consideradas como imagens geradas por outros
                 * apps, lan??ando tamb??m uma SecurityException para o usu??rio confirmar a exclus??o.
                 */
                contentResolver.delete(imageUri, null, null)
                withContext(Dispatchers.Main) {
                    Utils.showToast(this@ImageListActivity, "Image successfully deleted!")
                    reloadImageList()
                }
            } catch (e: SecurityException) {
                val intentSender = when {
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                        MediaStore.createDeleteRequest(contentResolver, listOf(imageUri)).intentSender
                    }
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                        /**
                         * A Uri da imagem est?? sendo salva para fazer um tratamento especial apenas
                         * para o Android 10 (Q), devido a um problema na exclus??o padr??o da imagem,
                         * algo que n??o acontece no Android 11 (R).
                         */
                        deletedImageUri = imageUri
                        val recoverableSecurityException = e as? RecoverableSecurityException
                        recoverableSecurityException?.userAction?.actionIntent?.intentSender
                    }
                    else -> null
                }

                intentSender?.let { sender ->
                    /**
                     * Uma caixa de di??logo ser?? exibida ao usu??rio para ele confirmar a exclus??o.
                     */
                    intentSenderLauncher.launch(
                        IntentSenderRequest.Builder(sender).build()
                    )
                }
            }
        }
    }

    private fun showDialogDeleteImage(imageUri: Uri) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Image")
            .setMessage("Do you really want to delete the image?")
            .setPositiveButton("Ok") { dialogInterface, pos ->
                lifecycleScope.launch {
                    deleteImage(imageUri)
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, pos ->
                dialogInterface.dismiss()
            }
            .show()
    }

    private fun reloadImageList() {
        lifecycleScope.launch {
            if (supportActionBar?.subtitle == "All Images") {
               imageListAdapter.updateImageList(loadAllImages())
            } else if (supportActionBar?.subtitle == "App Images") {
                imageListAdapter.updateImageList(loadAppImages())
            }
        }
    }
}