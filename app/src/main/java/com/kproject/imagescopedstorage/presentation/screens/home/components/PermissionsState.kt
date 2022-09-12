package com.kproject.imagescopedstorage.presentation.screens.home.components

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.theme.BackgroundColorPreview
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.theme.TextDefaultColor
import com.kproject.imagescopedstorage.presentation.utils.Utils

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsState(
    content: @Composable () -> Unit
) {
    val multiplePermissionsState = rememberMultiplePermissionsState(Utils.permissionList())
    var timesRequested by rememberSaveable { mutableStateOf(0) }

    if (multiplePermissionsState.allPermissionsGranted) {
        content.invoke()
    } else {
        when {
            (timesRequested <= 1) || multiplePermissionsState.shouldShowRationale -> {
                RationaleContent(
                    onLaunchMultiplePermissionRequest = {
                        timesRequested++
                        multiplePermissionsState.launchMultiplePermissionRequest()
                    }
                )
            }
            else -> {
                PermanentlyDeniedContent()
            }
        }
    }
}

@Composable
private fun RationaleContent(onLaunchMultiplePermissionRequest: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Text(
            text = stringResource(id = R.string.grant_storage_permission),
            color = TextDefaultColor,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                onLaunchMultiplePermissionRequest.invoke()
            },
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(12.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = MaterialTheme.colors.secondary
            )
        ) {
            Text(
                text = stringResource(id = R.string.request_permissions),
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
private fun PermanentlyDeniedContent() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.ic_mood_bad),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = CircleShape
                )
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.permissions_permanently_denied),
            color = TextDefaultColor,
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = BackgroundColorPreview)
@Composable
private fun RationaleContentPreview() {
    ImageScopedStorageTheme {
        RationaleContent(onLaunchMultiplePermissionRequest = { })
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = BackgroundColorPreview)
@Composable
private fun PermanentlyDeniedContentPreview() {
    ImageScopedStorageTheme {
        PermanentlyDeniedContent()
    }
}