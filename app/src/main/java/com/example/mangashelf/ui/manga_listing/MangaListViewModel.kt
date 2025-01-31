package com.example.mangashelf.ui.manga_listing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelf.data.repository.MangaRepository
import com.example.mangashelf.domain.model.GroupedMangas
import com.example.mangashelf.domain.model.MangaDto
import com.example.mangashelf.domain.model.MangaSortingOrder
import com.example.mangashelf.domain.model.toMangaDto
import com.example.mangashelf.util.NetworkResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MangaListViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
) : ViewModel() {

    private var pageSize = 20
    var navigatedToDetails = false
    private var pageNo = 0
    private var currentSortingOrder = MangaSortingOrder.PUBLISHED_YEAR
    private val fetchedMangaList: MutableList<MangaDto> = mutableListOf()

    data class MangaListUiState(
        val isLoading: Boolean = true,
        val totalMangas: Int = 0,
        val toastMessage: String? = null,
        val sortingOrder: MangaSortingOrder = MangaSortingOrder.PUBLISHED_YEAR,
        val errorMessage: String? = null,
        val showLoadingForSorting: Boolean = false,
        val groupedMangas: List<GroupedMangas> = emptyList(),
        val allFetchedMangas: List<MangaDto> = emptyList(),
        val mangasPublishedYears: List<Int> = emptyList()
    )

    private var _uiState: MutableStateFlow<MangaListUiState> = MutableStateFlow(MangaListUiState())

    val uiState get() = _uiState.asStateFlow()

    fun resetPagingDataAndChangeSortingOrder(changeSortOrderTo: MangaSortingOrder?) {
        pageNo = 0
        pageSize = 20
        changeSortOrderTo?.let {
            currentSortingOrder = it
        }
        _uiState.update {
            it.copy(
                sortingOrder = currentSortingOrder,
                showLoadingForSorting = true
            )
        }
        fetchedMangaList.clear()
    }

    fun getMangaListFromDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalCount = mangaRepository.getLocalMangasCount()
            val offset = pageNo * pageSize
            var updatedPageSize = pageSize

            if (offset >= totalCount) {
                return@launch
            }

            if (offset + pageSize >= totalCount) {
                updatedPageSize = totalCount - offset
            }

            val mangaList = when (currentSortingOrder) {
                MangaSortingOrder.SCORE -> {
                    mangaRepository.getLocalMangasPaginatedByScore(
                        limit = updatedPageSize,
                        offset = offset
                    )
                }

                MangaSortingOrder.PUBLISHED_YEAR -> {
                    mangaRepository.getLocalMangasPaginated(
                        limit = updatedPageSize,
                        offset = offset
                    )
                }

                MangaSortingOrder.POPULARITY -> {
                    mangaRepository.getLocalMangasPaginatedByPopularity(
                        limit = updatedPageSize,
                        offset = offset
                    )
                }
            }

            pageNo++

            fetchedMangaList.addAll(mangaList)
            val (publishedYears, groupedMangas) = withContext(Dispatchers.Default) {
                val years = fetchedMangaList.map { it.publishedYear }.distinct()
                val grouped = fetchedMangaList.groupBy { it.publishedYear }
                    .map { GroupedMangas(it.key, it.value) }
                years to grouped
            }

            _uiState.update {
                it.copy(
                    totalMangas = totalCount,
                    isLoading = false,
                    showLoadingForSorting = false,
                    allFetchedMangas = fetchedMangaList,
                    groupedMangas = groupedMangas,
                    mangasPublishedYears = publishedYears
                )
            }
        }
    }

    fun removeToastMessage() {
        _uiState.update {
            it.copy(toastMessage = null)
        }
    }

    fun getMangaListData() {
        viewModelScope.launch(Dispatchers.IO) {
            mangaRepository.fetchRemoteMangaList().collect { mangaResponse ->
                when (mangaResponse) {
                    is NetworkResponse.Error -> {
                        if (mangaRepository.getLocalMangasCount() > 0) {
                            _uiState.update {
                                it.copy(
                                    toastMessage = "Couldn't connect to the internet, showing offline results!",
                                    isLoading = false
                                )
                            }
                            getMangaListFromDb()
                        } else {
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Couldn't connect to the internet!",
                                    isLoading = false
                                )
                            }
                        }
                    }

                    is NetworkResponse.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }

                    is NetworkResponse.Success -> {
                        val favouriteMangasMap = mangaRepository.getFavouriteMangasFromDb()
                            .associateBy { it.id }
                        val updatedMangas = mangaResponse.data.map { apiManga ->
                            val isFavorite = favouriteMangasMap[apiManga.id.orEmpty()] != null
                            apiManga.copy(isFavourite = isFavorite)
                        }
                        val sortedMangaByYear = async(Dispatchers.Default) {
                            updatedMangas.map {
                                it.toMangaDto()
                            }.sortedBy { it.publishedYear }
                        }
                        mangaRepository.replaceAllMangas(sortedMangaByYear.await())
                        getMangaListFromDb()
                    }
                }
            }
        }
    }

}