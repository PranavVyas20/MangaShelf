package com.example.mangashelf.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Mangas")
data class MangaDto(
    @PrimaryKey
    val id: String,
    val imageUrl: String,
    val popularity: Long,
    val score: Float,
    val title: String,
    val category: String,
    val publishedYear: Int,
    val isFavourite: Boolean
)
