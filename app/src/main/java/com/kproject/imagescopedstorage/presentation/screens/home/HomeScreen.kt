package com.kproject.imagescopedstorage.presentation.screens.home

import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kproject.imagescopedstorage.R
import com.kproject.imagescopedstorage.presentation.screens.components.CustomImage
import com.kproject.imagescopedstorage.presentation.screens.components.FailureIndicator
import com.kproject.imagescopedstorage.presentation.screens.home.components.PermissionsState
import com.kproject.imagescopedstorage.presentation.screens.home.model.WebsiteOption
import com.kproject.imagescopedstorage.presentation.screens.home.model.websiteOptions
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme
import com.kproject.imagescopedstorage.presentation.utils.Utils
import com.skydoves.landscapist.coil.CoilImage

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    onNavigateToSavedImagesScreen: () -> Unit,
) {
    val context = LocalContext.current

    PermissionsState(
        content = {
            HomeScreenContent(
                onSaveImage = { bitmap ->
                    homeViewModel.saveImage(
                        bitmap = bitmap,
                        onSuccess = {
                            Utils.showToast(context, context.getString(R.string.image_saved_successfully))
                        },
                        onError = {
                            Utils.showToast(
                                context,
                                context.getString(R.string.error_saving_image),
                                Toast.LENGTH_LONG
                            )
                        }
                    )
                },
                onNavigateToSavedImagesScreen = onNavigateToSavedImagesScreen
            )
        }
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    onSaveImage: (Bitmap) -> Unit,
    onNavigateToSavedImagesScreen: () -> Unit
) {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var imageUrl by rememberSaveable { mutableStateOf(websiteOptions[0].url) }
    var selectedWebsiteOption by rememberSaveable { mutableStateOf(0) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
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

        WebsiteOptions(
            selectedOption = selectedWebsiteOption,
            onOptionSelected = { option ->
                selectedWebsiteOption = option
                imageUrl = websiteOptions[selectedWebsiteOption].generateRandomUrl()
                imageBitmap = null
            }
        )

        Spacer(Modifier.height(22.dp))

        Row {
            CustomButton(
                icon = R.drawable.ic_refresh,
                onClick = {
                    imageUrl = websiteOptions[selectedWebsiteOption].generateRandomUrl()
                    imageBitmap = null
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
private fun WebsiteOptions(
    modifier: Modifier = Modifier,
    selectedOption: Int,
    onOptionSelected: (Int) -> Unit,
) {
    var showDropdownMenu by rememberSaveable { mutableStateOf(false) }
    val dropdownIcon = if (showDropdownMenu) {
        ImageVector.vectorResource(id = R.drawable.ic_arrow_drop_up)
    } else {
        ImageVector.vectorResource(id = R.drawable.ic_arrow_drop_down)
    }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { showDropdownMenu = true }
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
        ) {
            Text(
                text = createBoldAnnotatedString(
                    text = websiteOptions[selectedOption].name,
                    color = Color(0xFFFFC400)
                ),
                color = Color.White,
                fontSize = 16.sp
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = dropdownIcon,
                contentDescription = null,
                tint = Color.White
            )
        }

        CustomDropdownMenu(
            showDropdownMenu = showDropdownMenu,
            onDismiss = { showDropdownMenu = false },
            options = websiteOptions,
            onOptionSelected = { option ->
                onOptionSelected.invoke(option)
            }
        )
    }
}

@Composable
private fun CustomDropdownMenu(
    modifier: Modifier = Modifier,
    showDropdownMenu: Boolean,
    onDismiss: () -> Unit,
    options: List<WebsiteOption>,
    onOptionSelected: (Int) -> Unit,
) {
    DropdownMenu(
        expanded = showDropdownMenu,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        options.forEachIndexed { index, websiteOptions ->
            DropdownMenuItem(
                onClick = {
                    onDismiss.invoke()
                    onOptionSelected.invoke(index)
                },
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier
                    .padding(4.dp)
                    .background(
                        color = MaterialTheme.colors.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Text(
                    text = createBoldAnnotatedString(
                        text = websiteOptions.name,
                        color = Color(0xFFFFC400)
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun CustomButton(
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

/**
 * Creates an AnnotatedString that will highlight text between <b> and </b> in bold.
 */
private fun createBoldAnnotatedString(
    text: String,
    color: Color
): AnnotatedString {
    val parts = text.split("<b>", "</b>")
    return buildAnnotatedString {
        var bold = false
        for (part in parts) {
            if (bold) {
                withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
                    append(part)
                }
            } else {
                append(part)
            }
            bold = !bold
        }
    }
}