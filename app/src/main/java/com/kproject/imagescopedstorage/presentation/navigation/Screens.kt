package com.kproject.imagescopedstorage.presentation.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object SavedImagesScreen : Screen("saved_images_screen")
    object ImageViewerScreen : Screen("image_viewer_screen")

    fun withArgs(vararg args: Any): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}