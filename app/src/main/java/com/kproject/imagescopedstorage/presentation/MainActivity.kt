package com.kproject.imagescopedstorage.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kproject.imagescopedstorage.presentation.navigation.NavigationGraph
import com.kproject.imagescopedstorage.presentation.screens.home.HomeScreen
import com.kproject.imagescopedstorage.presentation.screens.image.SavedImagesScreen
import com.kproject.imagescopedstorage.presentation.theme.ImageScopedStorageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ImageScopedStorageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NavigationGraph()
                }
            }
        }
    }
}