package com.kproject.imagescopedstorage.presentation.screens.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.model.Image
import com.kproject.imagescopedstorage.presentation.screens.components.CustomImage
import com.kproject.imagescopedstorage.presentation.screens.components.EmptyListInfo
import com.kproject.imagescopedstorage.presentation.screens.components.ProgressIndicator
import com.kproject.imagescopedstorage.presentation.screens.components.TopBar
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.utils.ViewState

@Composable
fun SavedImagesScreen(
    savedImagesViewModel: SavedImagesViewModel,
    onNavigateToImageViewerScreen: (imagePositionInTheList: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    /**
     * Get the images the first time only. Only LaunchedEffect() or ViewModel's init{} is
     * not enough in this case.
     */
    var imagesObtained by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (!imagesObtained) {
            savedImagesViewModel.getSavedImages()
            imagesObtained = true
        }
    }

    SavedImagesScreenContent(
        viewState = savedImagesViewModel.viewState,
        imageList = savedImagesViewModel.savedImagesList,
        onNavigateToImageViewerScreen = { imagePositionInTheList ->
            onNavigateToImageViewerScreen.invoke(imagePositionInTheList)
        },
        onNavigateBack = onNavigateBack
    )
}

@Composable
private fun SavedImagesScreenContent(
    viewState: ViewState,
    imageList: List<Image>,
    onNavigateToImageViewerScreen: (imagePositionInTheList: Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.saved_images),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            MainContent(
                viewState = viewState,
                imageList = imageList,
                onNavigateToImageViewerScreen = { imagePositionInTheList ->
                    onNavigateToImageViewerScreen.invoke(imagePositionInTheList)
                }
            )
        }
    }
}

@Composable
private fun MainContent(
    viewState: ViewState,
    imageList: List<Image>,
    onNavigateToImageViewerScreen: (imagePositionInTheList: Int) -> Unit
) {
    when (viewState) {
        ViewState.Loading -> {
            ProgressIndicator()
        }
        ViewState.Success -> {
            SavedImagesList(
                imageList = imageList,
                onNavigateToImageViewerScreen = { imagePositionInTheList ->
                    onNavigateToImageViewerScreen.invoke(imagePositionInTheList)
                }
            )
        }
        ViewState.Empty -> {
            EmptyListInfo(
                iconResId = R.drawable.ic_photo_library,
                title = stringResource(id = R.string.empty_image_list)
            )
        }
        ViewState.Error -> {
            EmptyListInfo(
                iconResId = R.drawable.ic_broken_image,
                title = stringResource(id = R.string.error_get_image_list)
            )
        }
    }
}

@Composable
private fun SavedImagesList(
    imageList: List<Image>,
    onNavigateToImageViewerScreen: (imagePositionInTheList: Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(
                items = imageList,
                key = { index, image -> image.id }
            ) { index, image ->
                SavedImageListItem(
                    image = image,
                    onClick = {
                        onNavigateToImageViewerScreen.invoke(index)
                    },
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
private fun SavedImageListItem(
    modifier: Modifier = Modifier,
    image: Image,
    onClick: () -> Unit
) {
    Card(
        elevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
            .aspectRatio(1f)
            .clickable {
                onClick.invoke()
            }
    ) {
        CustomImage(
            imageModel = image.contentUri,
            modifier = Modifier.aspectRatio(1f)
        )
    }
}

@Preview(showSystemUi = true)
@Composable
private fun Preview() {
    ImageScopedStorageTheme {
        SavedImagesScreenContent(
            viewState = ViewState.Success,
            imageList = fakeImageList,
            onNavigateToImageViewerScreen = { },
            onNavigateBack =  { }
        )
    }
}
