package com.kproject.imagescopedstorage.presentation.screens.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = { },
    backgroundColor: Color = MaterialTheme.colors.primary
) {
    TopAppBar(
        title = { Text(text = title, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        actions = actions,
        backgroundColor = backgroundColor,
        contentColor = Color.White,
        elevation = 0.dp,
        modifier = modifier
    )
}