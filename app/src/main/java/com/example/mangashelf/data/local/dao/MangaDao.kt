package com.example.mangashelf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.mangashelf.domain.model.MangaDto

@Dao
interface MangaDao {
    @Query("SELECT * FROM mangas LIMIT :limit OFFSET :offset")
    suspend fun getPagedMangas(limit: Int, offset: Int): List<MangaDto>

    @Query("SELECT * FROM Mangas ORDER BY score DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedMangasByScore(limit: Int = 30, offset: Int): List<MangaDto>

    @Query("SELECT * FROM Mangas ORDER BY popularity DESC LIMIT :limit OFFSET :offset")
    suspend fun getPagedMangasByPopularity(limit: Int = 30, offset: Int): List<MangaDto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMangas(items: List<MangaDto>)

    @Query("UPDATE Mangas SET isFavourite = :isFavourite WHERE id = :id")
    suspend fun updateMangaFavouriteStatus(id: String, isFavourite: Boolean)

    @Query("SELECT * FROM Mangas WHERE isFavourite = 1")
    suspend fun getFavouritesMangasFromDb(): List<MangaDto>

    @Query("SELECT COUNT(*) FROM Mangas")
    suspend fun getItemCountFromDb(): Int

    @Query("DELETE FROM mangas")
    suspend fun deleteAllMangas()

    @Query("SELECT * FROM Mangas WHERE id = :id")
    suspend fun getMangaById(id: String): MangaDto?

    @Transaction
    suspend fun replaceAllMangas(items: List<MangaDto>) {
        deleteAllMangas()
        insertAllMangas(items)
    }
}