package com.kproject.imagescopedstorage.presentation.screens.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kproject.imagescopedstorage.R
import com.skydoves.landscapist.ShimmerParams
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun CustomImage(
    modifier: Modifier = Modifier,
    imageModel: Any,
    contentScale: ContentScale = ContentScale.Crop
) {
    CoilImage(
        imageModel = imageModel,
        contentScale = contentScale,
        shimmerParams = shimmerParams(),
        failure = {
            FailureIndicator()
        },
        previewPlaceholder = R.drawable.ic_photo,
        modifier = modifier
    )
}

@Composable
private fun shimmerParams() = ShimmerParams(
    baseColor = MaterialTheme.colors.background,
    highlightColor = MaterialTheme.colors.onSurface,
    durationMillis = 350,
    dropOff = 0.65f,
    tilt = 20f
)

@Composable
fun BoxScope.FailureIndicator() {
    Box(
        modifier = Modifier
            .matchParentSize()
            .background(Color(0x99AA0003))
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_broken_image),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color(0xFFCECECE)),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(12.dp)
        )
    }
}