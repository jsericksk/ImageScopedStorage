package com.kproject.imagescopedstorage.presentation.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.theme.BackgroundColorPreview
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.theme.TextDefaultColor

@Composable
fun EmptyListInfo(
    modifier: Modifier = Modifier,
    @DrawableRes iconResId: Int,
    title: String
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colors.primary
        )
        Text(
            text = title,
            textAlign = TextAlign.Center,
            color = TextDefaultColor,
            fontSize = 18.sp,
            fontWeight = Bold,
            modifier = Modifier.padding(all = 6.dp)
        )
    }
}

@Preview(showSystemUi = true, showBackground = true, backgroundColor = BackgroundColorPreview)
@Composable
private fun Preview() {
    ImageScopedStorageTheme {
        EmptyListInfo(
            iconResId = R.drawable.ic_photo_library,
            title = stringResource(id = R.string.empty_image_list)
        )
    }
}