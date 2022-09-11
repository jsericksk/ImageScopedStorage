package com.kproject.imagescopedstorage.presentation.screens.home

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.screens.components.AlertDialog
import com.kproject.imagescopedstorage.presentation.screens.components.CustomImage
import com.kproject.imagescopedstorage.presentation.screens.components.FailureIndicator
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.utils.Utils
import com.skydoves.landscapist.coil.CoilImage
import kotlin.random.Random

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToSavedImagesScreen: () -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val multiplePermissionState = rememberMultiplePermissionsState(Utils.permissionList())

    var isPermissionRequested by rememberSaveable { mutableStateOf(false) }
    var showDialogRequestPermission by remember { mutableStateOf(false) }
    var showDialogPermissionPermanentlyDenied by remember { mutableStateOf(false) }

    HomeScreenContent(
        onSaveImage = { bitmap ->

        },
        onNavigateToSavedImagesScreen = onNavigateToSavedImagesScreen
    )

    AlertDialog(
        showDialog = showDialogRequestPermission,
        onDismiss = { showDialogRequestPermission = false },
        title = stringResource(id = R.string.dialog_storage_permission_denied),
        message = stringResource(id = R.string.dialog_grant_storage_permission),
        onClickButtonOk = { multiplePermissionState.launchMultiplePermissionRequest() },
    )

    AlertDialog(
        showDialog = showDialogPermissionPermanentlyDenied,
        onDismiss = { showDialogPermissionPermanentlyDenied = false },
        title = stringResource(id = R.string.dialog_storage_permission_denied),
        message = stringResource(id = R.string.dialog_permissions_permanently_denied),
        showButtonCancel = false,
        onClickButtonOk = { showDialogPermissionPermanentlyDenied = false }
    )
}

private const val Url = "https://thiscatdoesnotexist.com"

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onSaveImage: (Bitmap) -> Unit,
    onNavigateToSavedImagesScreen: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUrl by rememberSaveable { mutableStateOf(Url) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize(),
    ) {
        CoilImage(
            imageModel = imageUrl,
            success = { imageState ->
                imageState.drawable?.toBitmap()?.let { bitmap ->
                    CustomImage(
                        imageModel = bitmap,
                        modifier = Modifier
                            .size(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    imageBitmap = bitmap
                }
            },
            loading = {
                CircularProgressIndicator(
                    modifier = Modifier.matchParentSize()
                )
            },
            failure = {
                FailureIndicator()
            },
            previewPlaceholder = R.drawable.ic_photo,
            modifier = Modifier.size(300.dp)
        )

        Spacer(Modifier.height(22.dp))

        Row {
            CustomButton(
                icon = R.drawable.ic_refresh,
                onClick = {
                    imageUrl = "$Url/?${Random.nextInt()}"
                }
            )
            Spacer(Modifier.width(24.dp))
            CustomButton(
                icon = R.drawable.ic_save,
                onClick = {
                    imageBitmap?.let { bitmap ->
                        onSaveImage.invoke(bitmap)
                    }
                }
            )
        }

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = onNavigateToSavedImagesScreen,
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            )
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_photo_library),
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.width(14.dp))
            Text(
                text = stringResource(id = R.string.saved_images),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .background(color = MaterialTheme.colors.secondary, shape = CircleShape)
            .padding(8.dp)
            .size(40.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(36.dp)
        )
    }
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
private fun Preview() {
    ImageScopedStorageTheme {
        HomeScreenContent(
            onSaveImage = { },
            onNavigateToSavedImagesScreen = { }
        )
    }
}
