package com.kproject.imagescopedstorage.presentation.screens.image

import android.app.Activity.RESULT_OK
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.kproject.imagescopedstorage.presentation.model.Image
import com.kproject.imagescopedstorage.presentation.screens.components.AlertDialog
import com.kproject.imagescopedstorage.presentation.screens.components.CustomImage
import com.kproject.imagescopedstorage.presentation.screens.components.TopBar
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme

@Composable
fun ImageViewerScreen(
    imagePositionInTheList: Int,
    savedImagesViewModel: SavedImagesViewModel,
    onNavigateBack: () -> Unit
) {
    val imageList = savedImagesViewModel.savedImagesList
    val imageViewerUiState = savedImagesViewModel.imageViewerState

    // Will only be used on Android 11+ if a SecurityException is thrown
    val deleteImageLauncher =
            rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { activityResult->
                if (activityResult.resultCode == RESULT_OK) {
                    savedImagesViewModel.removeImageFromList(imageViewerUiState.currentPage)
                }
            }

    ImageViewerScreenContent(
        imagePositionInTheList = imagePositionInTheList,
        imageList = imageList,
        onDeleteImage = {
            savedImagesViewModel.deleteImage(
                imageUri = imageList[imageViewerUiState.currentPage].contentUri,
                index = imageViewerUiState.currentPage,
                intentSenderLauncher = deleteImageLauncher
            )
        },
        onNavigateBack = onNavigateBack,
        onCurrentPageChange = { currentPage ->
            savedImagesViewModel.onCurrentPageChange(currentPage)
        },
    )
}

@Composable
private fun ImageViewerScreenContent(
    imagePositionInTheList: Int,
    imageList: List<Image>,
    onDeleteImage: () -> Unit,
    onNavigateBack: () -> Unit,
    onCurrentPageChange: (Int) -> Unit,
) {
    var topBarTitle by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopBar(
                title = topBarTitle,
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            SavedImageList(
                initialPosition = imagePositionInTheList,
                imageList = imageList,
                onDeleteImage = onDeleteImage,
                onNavigateBack = onNavigateBack,
                onTopBarTitleChange = { title ->
                    topBarTitle = title
                },
                onCurrentPageChange = { currentPage ->
                    onCurrentPageChange.invoke(currentPage)
                }
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun SavedImageList(
    modifier: Modifier = Modifier,
    initialPosition: Int,
    imageList: List<Image>,
    onDeleteImage: () -> Unit,
    onNavigateBack: () -> Unit,
    onTopBarTitleChange: (String) -> Unit,
    onCurrentPageChange: (Int) -> Unit,
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(initialPage = initialPosition)
    var currentPage by rememberSaveable { mutableStateOf(initialPosition) }

    LaunchedEffect(imageList.size) {
        if (imageList.isEmpty()) {
            onNavigateBack.invoke()
        }
    }

    LaunchedEffect(currentPage) {
        onCurrentPageChange.invoke(currentPage)
        if (imageList.isNotEmpty()) {
            val title = imageList[currentPage].displayName.replace(".png", "")
            onTopBarTitleChange.invoke(title)
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        HorizontalPager(
            count = imageList.size,
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Box {
                ImageItem(imageUri = imageList[page].contentUri)

                ItemIndexIndicator(
                    currentIndex = page,
                    imageListSize = imageList.size,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                )
            }
        }
        currentPage = pagerState.currentPage

        BottomBar(
            onDeleteImage = {
                onDeleteImage.invoke()
            }
        )
    }
}

@Composable
private fun ImageItem(
    modifier: Modifier = Modifier,
    imageUri: Uri
) {
    CustomImage(
        imageModel = imageUri,
        contentScale = ContentScale.Fit,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
private fun ItemIndexIndicator(
    modifier: Modifier = Modifier,
    currentIndex: Int,
    imageListSize: Int
) {
    Text(
        text = "${currentIndex + 1} / $imageListSize",
        fontSize = 14.sp,
        color = Color.White,
        modifier = modifier
            .background(
                color = MaterialTheme.colors.primary,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(6.dp)
    )
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    onDeleteImage: () -> Unit
) {
    var showDialogDeleteImage by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.primary)
            .padding(6.dp)
    ) {
        IconButton(onClick = { showDialogDeleteImage = true }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = Color.White
            )
        }
    }

    AlertDialog(
        showDialog = showDialogDeleteImage,
        onDismiss = { showDialogDeleteImage = false },
        title = stringResource(id = com.kproject.imagescopedstorage.R.string.delete_image),
        message = stringResource(id = com.kproject.imagescopedstorage.R.string.delete_image_confirmation),
        onClickButtonOk = onDeleteImage
    )
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    ImageScopedStorageTheme {
        ImageViewerScreenContent(
            imagePositionInTheList = 0,
            imageList = fakeImageList,
            onDeleteImage = { },
            onNavigateBack = { },
            onCurrentPageChange = { },
        )
    }
}