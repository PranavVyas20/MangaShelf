package com.example.mangashelf.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mangashelf.data.local.dao.MangaDao
import com.example.mangashelf.domain.model.MangaDto

@Database(entities = [MangaDto::class], version = 5)
abstract class MangaDatabase: RoomDatabase() {
    abstract fun mangaDao(): MangaDao
}