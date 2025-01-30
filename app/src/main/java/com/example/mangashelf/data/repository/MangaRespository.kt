package com.example.mangashelf.data.repository

import com.example.mangashelf.data.local.dao.MangaDao
import com.example.mangashelf.data.remote.MangaRemoteApi
import com.example.mangashelf.domain.model.MangaDto
import javax.inject.Inject

class MangaRepository @Inject constructor(
    private val mangaRemoteApi: MangaRemoteApi,
    private val mangaDao: MangaDao
) : BaseRepository {
    suspend fun fetchRemoteMangaList() = getFlowResult {
        mangaRemoteApi.fetchMangaList()
    }

    suspend fun getLocalMangasCount() = mangaDao.getItemCountFromDb()

    suspend fun replaceAllMangas(items: List<MangaDto>) = mangaDao.replaceAllMangas(items)

    suspend fun updateMangaFavouriteStatus(mangaId: String, isFavourite: Boolean) =
        mangaDao.updateMangaFavouriteStatus(id = mangaId, isFavourite = isFavourite)

    suspend fun getLocalMangasPaginated(limit: Int = 30, offset: Int) =
        mangaDao.getPagedMangas(limit, offset)

    suspend fun getLocalMangasPaginatedByScore(limit: Int = 30, offset: Int) =
        mangaDao.getPagedMangasByScore(limit = limit, offset = offset)

    suspend fun getLocalMangasPaginatedByPopularity(limit: Int = 30, offset: Int) =
        mangaDao.getPagedMangasByPopularity(limit = limit, offset = offset)

    suspend fun getFavouriteMangasFromDb() = mangaDao.getFavouritesMangasFromDb()

    suspend fun getMangaById(id: String) = mangaDao.getMangaById(id)
}