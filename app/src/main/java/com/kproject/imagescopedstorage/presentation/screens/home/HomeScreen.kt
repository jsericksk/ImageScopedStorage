package com.kproject.imagescopedstorage.presentation.screens.home

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToSavedImagesScreen: () -> Unit
) {
    val permissionList = remember {
        val readPermission = if (Build.VERSION.SDK_INT >= 33) {
            android.Manifest.permission.READ_MEDIA_IMAGES
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            listOf(readPermission)
        } else {
            listOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
    val multiplePermissionState = rememberMultiplePermissionsState(permissionList)

}

@OptIn(ExperimentalPermissionsApi::class)
private fun checkPermissionState(
    permissionsState: MultiplePermissionsState,
    isPermissionRequested: Boolean,
    hasPermission: () -> Unit,
    shouldShowRationale: () -> Unit,
    permissionPermanentlyDenied: () -> Unit,
) {
    if (permissionsState.allPermissionsGranted) {
        hasPermission.invoke()
    } else {
        when {
            !permissionsState.shouldShowRationale && !isPermissionRequested -> {
                permissionsState.launchMultiplePermissionRequest()
            }
            permissionsState.shouldShowRationale -> {
                shouldShowRationale.invoke()
            }
            else -> {
                permissionPermanentlyDenied.invoke()
            }
        }
    }
}