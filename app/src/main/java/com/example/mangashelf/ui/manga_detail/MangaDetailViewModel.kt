package com.example.mangashelf.ui.manga_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelf.data.repository.MangaRepository
import com.example.mangashelf.domain.model.MangaDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailViewModel @Inject constructor(private val mangaRepository: MangaRepository) : ViewModel() {

    private var _uiState: MutableStateFlow<MangaDetailUiState> =
        MutableStateFlow(MangaDetailUiState())
    val uiState get() = _uiState

    fun getMangaDetails(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mangaRepository.getMangaById(id)?.let { manga ->
                _uiState.update {
                    it.copy(mangaDetails = manga, chapterNameRandom = (1..20).random())
                }
            }
        }
    }

    fun updateFavouriteStatusForManga(mangaId: String, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            mangaRepository.updateMangaFavouriteStatus(mangaId, isFavourite)
        }
    }
}

data class MangaDetailUiState(
    val isLoading: Boolean = true,
    val chapterNameRandom: Int = 3,
    val mangaDetails: MangaDto? = null
)
