package com.kproject.imagescopedstorage.presentation.screens.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.theme.BackgroundColorPreview
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.theme.TextDefaultColor

@Composable
fun AlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    cancelable: Boolean = true,
    onClickButtonOk: () -> Unit,
    onClickButtonCancel: () -> Unit = {},
    showButtonCancel: Boolean = true
) {
    if (showDialog) {
        CustomAlertDialog(
            showDialog = showDialog,
            onDismiss = onDismiss,
            title = title,
            cancelable = cancelable,
            onClickButtonOk = onClickButtonOk,
            onClickButtonCancel = onClickButtonCancel,
            showButtonCancel = showButtonCancel,
            content = {
                Text(
                    text = message,
                    color = TextDefaultColor,
                    fontSize = 16.sp
                )
            }
        )
    }
}

@Composable
private fun CustomAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String,
    cancelable: Boolean = true,
    onClickButtonOk: () -> Unit,
    onClickButtonCancel: () -> Unit = {},
    showButtonCancel: Boolean = true,
    content: @Composable () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (cancelable) {
                    onDismiss.invoke()
                }
            },
            title = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = TextDefaultColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(Modifier.height(12.dp))
                    content.invoke()
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDismiss.invoke()
                        onClickButtonOk.invoke()
                    }
                ) {
                    Text(
                        text = stringResource(id = R.string.button_ok).uppercase(),
                        color = MaterialTheme.colors.secondary
                    )
                }
            },
            dismissButton = {
                if (showButtonCancel) {
                    TextButton(
                        onClick = {
                            onDismiss.invoke()
                            onClickButtonCancel.invoke()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.button_cancel).uppercase(),
                            color = MaterialTheme.colors.secondary
                        )
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = BackgroundColorPreview)
@Composable
private fun Preview() {
    ImageScopedStorageTheme {
        AlertDialog(
            showDialog = true,
            onDismiss = { },
            title = "Alert Dialog",
            message = "Alert dialog message.",
            onClickButtonOk = { }
        )
    }
}
