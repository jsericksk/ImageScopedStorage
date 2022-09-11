package com.kproject.imagescopedstorage.presentation.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.kproject.imagescopedstorage.presentation.screens.home.HomeScreen
import com.kproject.imagescopedstorage.presentation.screens.image.ImageViewerScreen
import com.kproject.imagescopedstorage.presentation.screens.image.SavedImagesScreen
import com.kproject.imagescopedstorage.presentation.screens.image.SavedImagesViewModel

private const val ArgImagePositionInTheList = "imagePositionInTheList"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavigationGraph() {
    val navController = rememberAnimatedNavController()
    val savedImagesViewModel: SavedImagesViewModel = viewModel()

    AnimatedNavHost(navController = navController, startDestination = Screen.HomeScreen.route) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                onNavigateToSavedImagesScreen = {
                    navController.navigate(Screen.SavedImagesScreen.route)
                }
            )
        }

        /**
         * SavedImagesScreen
         */
        composable(
            route = Screen.SavedImagesScreen.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            }
        ) {
            SavedImagesScreen(
                savedImagesViewModel = savedImagesViewModel,
                onNavigateToImageViewerScreen = { imagePositionInTheList ->
                    navController.navigate(Screen.ImageViewerScreen.withArgs(imagePositionInTheList))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        /**
         * ImageViewerScreen
         */
        composable(
            route = Screen.ImageViewerScreen.route + "/{$ArgImagePositionInTheList}",
            arguments = listOf(
                navArgument(name = ArgImagePositionInTheList) {
                    type = NavType.IntType
                }
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Up,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Down,
                    animationSpec = tween(700)
                )
            }
        ) { entry ->
            ImageViewerScreen(
                imagePositionInTheList = entry.arguments!!.getInt(ArgImagePositionInTheList),
                savedImagesViewModel = savedImagesViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}