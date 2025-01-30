package com.example.mangashelf.domain.model

data class GroupedMangas(
    val publishedYear: Int,
    val mangas: List<MangaDto>
)
