package com.example.mangashelf.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mangashelf.ui.manga_detail.MangaDetailScreen
import com.example.mangashelf.ui.manga_detail.MangaDetailViewModel
import com.example.mangashelf.ui.manga_listing.MangaListScreen
import com.example.mangashelf.ui.manga_listing.MangaListViewModel

@Composable
fun MangaNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "MangaListScreen") {
        composable("MangaListScreen") { entry ->
            val viewModel: MangaListViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            LaunchedEffect(Unit) {
                if (entry.savedStateHandle.get<Boolean>("shouldFetchFromDb") == true) {
                    viewModel.resetPagingDataAndChangeSortingOrder(null)
                    viewModel.getMangaListFromDb()
                } else if (!viewModel.navigatedToDetails) {
                    viewModel.getMangaListData()
                    viewModel.navigatedToDetails = false
                }
            }
            MangaListScreen(
                uiState = uiState,
                onLoadMoreData = {
                    viewModel.getMangaListFromDb()
                },
                onMangaItemClicked = { mangaId ->
                    viewModel.navigatedToDetails = true
                    navController.navigate("MangaDetailScreen/$mangaId")
                },
                onRetryClicked = {
                    viewModel.getMangaListData()
                },
                removeToastMessage = { viewModel.removeToastMessage() },
                onSortByClicked = { sortBy ->
                    if (uiState.sortingOrder != sortBy) {
                        viewModel.resetPagingDataAndChangeSortingOrder(sortBy)
                        viewModel.getMangaListFromDb()
                    }
                })
        }

        composable(
            route = "MangaDetailScreen/{mangaId}",
            arguments = listOf(
                navArgument("mangaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val viewModel: MangaDetailViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val mangaId = backStackEntry.arguments?.getString("mangaId") ?: ""
            LaunchedEffect(Unit) {
                viewModel.getMangaDetails(mangaId)
            }
            MangaDetailScreen(
                uiState = uiState,
                navController = navController,
                onSaveToDbClick = { shouldMarkFavorite ->
                    viewModel.updateFavouriteStatusForManga(
                        mangaId = mangaId,
                        isFavourite = shouldMarkFavorite
                    )
                })
        }
    }
}